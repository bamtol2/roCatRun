package com.eeos.rocatrun.game

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.security.identity.ResultData
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.eeos.rocatrun.result.SingleWinScreen
import com.eeos.rocatrun.socket.SocketHandler
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.PutDataMapRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

class GamePlay : ComponentActivity(), DataClient.OnDataChangedListener {
    private lateinit var dataClient: DataClient
    private var runningData by mutableStateOf<RunningData?>(null)
    private var resultData by mutableStateOf<ResultData?>(null)
    private var playersData by mutableStateOf<PlayersData?>(null)
    private var gpxFileReceived by mutableStateOf(false)

    // 실시간 러닝 데이터
    data class RunningData(
        val totalDistance: Double,
        val elapsedTime: Long
    )

    // 실시간 팀원들 데이터
    data class PlayersData(
        val nickName: String,
        val totalDistance: Double,
        val totalItemUsage: Int,
    )

    // 본인 결과 데이터
    data class ResultData(
        val totalDistance: Double,
        val elapsedTime: Long,
        val averagePace: Double,
        val averageHeartRate: Double,
//        val averageCadence?? - 케이던스 어케할건징
//        val totalItemUsage: Int,
    )

    // 유저들 게임 결과 데이터
    data class PlayersResultData(
        val userId: String,
        val nickName: String,
        val characterImage: String,  // 유저의 프로필 이미지 파일 경로
        val runningTime: Long,
        val totalDistance: Double,
        val paceAvg: Double,
        val heartRateAvg: Double,
        val cadenceAvg: Double,
        val calories: Int,
        val itemUseCount: Int,
        val rewardExp: Int,
        val rewardCoin: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // intent로 전달된 bossHealth 추출
        val firstBossHealth = intent.getIntExtra("firstBossHealth", 100000)
        val playerNicknames = intent.getStringArrayListExtra("playerNicknames")

        dataClient = Wearable.getDataClient(this)

        Log.d("Socket", "페이지 이동 후 표출 $firstBossHealth $playerNicknames")

        // 워치 앱을 시작하는 함수
        fun startWatchApp(context: Context) {
            val messageClient: MessageClient = Wearable.getMessageClient(context)
            val path = "/start_watch_app"
            val messageData = "Start Game".toByteArray()

            Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener { nodes ->
                if (nodes.isNotEmpty()) {
                    val nodeId = nodes.first().id
                    Log.d("WearApp", "연결된 노드: ${nodes.first().displayName}")

                    messageClient.sendMessage(nodeId, path, messageData).apply {
                        addOnSuccessListener {
                            Log.d("Wear APP", "메시지 전송 성공")
                            Toast.makeText(context, "워치 앱 시작 요청 전송 완료", Toast.LENGTH_SHORT).show()

                            // 게임 초기 데이터 전송
                            if (playerNicknames != null) {
                                gameStartEvent(firstBossHealth, playerNicknames)
                            }
                        }
                        addOnFailureListener { exception ->
                            Log.e("Wear APP", "메시지 전송 실패: ${exception.message}")
                            Toast.makeText(context, "워치 앱 시작 요청 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.d("WearApp", "연결된 노드가 없습니다.")
                    Toast.makeText(context, "연결된 디바이스가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Log.e("WearApp", "노드 검색 실패: ${exception.message}")
                Toast.makeText(context, "워치 연결 확인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        // 메세지 보내기
        startWatchApp(this)

        // 웹소켓 유저들 러닝데이터 수신
        playerDataUpdatedSocket()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb()
            )
        )

        setContent {
            RoCatRunTheme(
                darkTheme = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameplayScreen(
                        gpxFileReceived = gpxFileReceived,
                        onShareClick = { shareLatestGpxFile() }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                when (dataItem.uri.path) {
                    "/running_data" -> processRunningData(dataItem)
                    "/final_result_data" -> processResultData(dataItem)
                    "/use_item" -> processUseItem(dataItem)
                    "/gpx_data" -> processGpxData(dataItem)
                }
            }
        }
    }

    // 워치에서 실시간 러닝데이터 받아오는 함수
    private fun processRunningData(dataItem: DataItem) {
        DataMapItem.fromDataItem(dataItem).dataMap.apply {
            runningData = RunningData(
                totalDistance = getDouble("distance"),
                elapsedTime = getLong("time")
            )
        }

        Log.d("Wear","러닝 데이터 받는중 - $runningData")

        // 웹소켓으로 전송
        runningData?.let { data ->
            updateRunDataSocket(data.totalDistance)
        }
    }

    // 워치에서 게임 결과 받아오는 함수
    private fun processResultData(dataItem: DataItem) {
        DataMapItem.fromDataItem(dataItem).dataMap.apply {
            resultData = ResultData(
                totalDistance = getDouble("distance"),
                elapsedTime = getLong("time"),
                averagePace = getDouble("averagePace"),
                averageHeartRate = getDouble("averageHeartRate")
//                averageCadence = getDouble("averageCadence")
            )
        }

        Log.d("Wear", "게임 결과 데이터 수신 완료! - $resultData")

        // 웹소켓으로 전송
        resultData?.let { data ->
            submitRunningResultSocket(
                data.elapsedTime, 
                data.totalDistance, 
                data.averagePace,
                data.averageHeartRate,
                0.0              // 케이던스...
            )
        }
    }

    // 워치에서 아이템 사용여부 받아오는 함수
    private fun processUseItem(dataItem: DataItem) {
        DataMapItem.fromDataItem(dataItem).dataMap.apply {

            // 공격 여부 워치에서 받기
            val itemUsed = getBoolean("itemUsed")
            Log.d("Wear", "아이템 사용 여부 받음: $itemUsed")
        }

        Log.d("Wear","공격")

        // 웹소켓 이벤트 송신 - useItem
        SocketHandler.mSocket.emit("useItem")
    }

    private fun processGpxData(dataItem: DataItem) {
        val asset = DataMapItem.fromDataItem(dataItem).dataMap.getAsset("gpx_file")
        asset?.let {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = Wearable.getDataClient(this@GamePlay).getFdForAsset(it).await()
                    response.inputStream?.use { stream ->
                        val gpxContent = stream.bufferedReader().use { it.readText() }
                        saveGpxFile(gpxContent)
                        gpxFileReceived = true
                    }

                } catch (e: IOException) {
                    Log.e("GameMulti", "Error processing GPX data", e)
                }
            }
        }
    }

    private fun saveGpxFile(content: String) {
        try {
            val file = File(getExternalFilesDir(null), "activity_${System.currentTimeMillis()}.gpx")
            FileOutputStream(file).use { it.write(content.toByteArray()) }
            Log.d("GameMulti", "GPX file saved: ${file.absolutePath}")
        } catch (e: IOException) {
            Log.e("GameMulti", "Error saving GPX file", e)
        }
    }

    private fun shareLatestGpxFile() {
        val directory = getExternalFilesDir(null)
        val gpxFiles = directory?.listFiles { file -> file.name.endsWith(".gpx") }

        gpxFiles?.maxByOrNull { it.lastModified() }?.let { latestFile ->
            shareGpxFile(this, latestFile.name)
        } ?: run {
            // GPX 파일이 없을 경우 처리
            Log.e("GameMulti", "No GPX files found to share")
        }
    }

    private fun shareGpxFile(context: Context, fileName: String) {
        val file = File(context.getExternalFilesDir(null), fileName)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/gpx+xml"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "GPX 파일 공유"))
    }

    // 웹소켓 - 게임 스타트 수신
    private fun gameStartEvent(firstBossHealth: Int, playerNicknames: ArrayList<String>){

        // 워치에 초기 boss health 보내기
        val putDataMapRequest = PutDataMapRequest.create("/first_boss_health")
        putDataMapRequest.dataMap.apply {
            putInt("firstBossHealth",firstBossHealth)
            // 닉네임 ArrayList 추가
            putStringArrayList("playerNicknames", playerNicknames)
        }
        val request = putDataMapRequest.asPutDataRequest().setUrgent()
        dataClient.putDataItem(request)
            .addOnSuccessListener { _ ->
                Log.d("Wear", "보스 초기 체력 송신")
            }
            .addOnFailureListener { exception ->
                Log.e("Wear", "보스 초기 체력 송신 실패", exception)
            }
    }

    // 웹소켓 - 실시간 러닝데이터 송신
    private fun updateRunDataSocket(
        distance: Double)
    {
        // 전송할 JSON 생성: {"runningData": {"distance": 5.2, "runningTime": 123}}
        val runningDataPayload = JSONObject().apply {
            put("distance", distance)
        }
        val runDataJson = JSONObject().apply {
            put("runningData", runningDataPayload)
        }
        Log.d("Socket", "Emit - updateRunningData: $runDataJson")

        // updateRunningData 실시간 러닝 데이터 전송
        SocketHandler.mSocket.emit("updateRunningData", runDataJson)

    }

    // 웹소켓 - 플레이어들 실시간 데이터 수신
    private fun playerDataUpdatedSocket(){

        // 서버에서 updateRunningData 응답 받기
        SocketHandler.mSocket.on("updateRunningData") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val responseJson = args[0] as JSONObject
                val nickName = responseJson.optString("nickName", "unknown")
                val returnedDistance = responseJson.optDouble("distance", 0.0)
                val itemUseCount = responseJson.optInt("itemUseCount", 0)
                Log.d(
                    "Socket", "On - updateRunningData: " +
                            "nickName: $nickName, distance: $returnedDistance, itemUseCount: $itemUseCount"
                )

                // 워치에 보낼 playersData 에 위 정보 넣어서 그대로 보내기
                playersData = PlayersData(
                    nickName = nickName,
                    totalDistance = returnedDistance,
                    totalItemUsage = itemUseCount
                )

                // 업데이트된 playersData를 워치에 전송하기 위해 PutDataMapRequest 생성
                val putDataMapRequest = PutDataMapRequest.create("/players_data")
                putDataMapRequest.dataMap.apply {
                    putString("nickName", nickName)
                    putDouble("distance", returnedDistance)
                    putInt("itemUseCount", itemUseCount)
                }
                val request = putDataMapRequest.asPutDataRequest().setUrgent()
                dataClient.putDataItem(request)
                    .addOnSuccessListener { _ ->
                        Log.d("Wear", "플레이어들 데이터 전송 완료")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Wear", "플레이어들 데이터 전송 실패", exception)
                    }

            }
        }
    }

    // 웹소켓 - 게임 결과 데이터 송신
    private fun submitRunningResultSocket(
        runningTime: Long,
        totalDistance: Double,
        paceAvg: Double,
        heartRateAvg: Double,
        cadenceAvg: Double
    ){
        val runningResultJson = JSONObject().apply {
            put("runningTime", runningTime)
            put("totalDistance", totalDistance)
            put("paceAvg", paceAvg)
            put("heartRateAvg", heartRateAvg)
            put("cadenceAvg", cadenceAvg)
        }

        // 본인 러닝 결과 전송
        SocketHandler.mSocket.emit("submitRunningResult", runningResultJson)
        Log.d("Socket", "Emit - submitRunningResult: $runningResultJson")
    }
}
