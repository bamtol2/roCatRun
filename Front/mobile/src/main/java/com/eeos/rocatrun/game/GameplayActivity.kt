package com.eeos.rocatrun.game

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.security.identity.ResultData
import android.util.Log
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
        val userId: String,
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
        val runningTime: Long,
        val totalDistance: Double,
        val paceAvg: Double,
        val heartRateAvg: Double,
        val cadenceAvg: Double,
        val calories: Int,
        val itemUseCount: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataClient = Wearable.getDataClient(this)
        gameStartSocket()
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
            updateRunDataSocket(data.totalDistance, data.elapsedTime)
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
//                totalItemUsage = getInt("totalItemUsage")
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
    private fun gameStartSocket(){
        // 게임 스타트 이벤트 시작
        SocketHandler.mSocket.on("gameStart") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val json = args[0] as JSONObject
                val firstBossHealth = json.optInt("bossHp", 10000)

                Log.d("Socket", "On - gameStart")

                // 워치에 초기 boss health 보내기
                val putDataMapRequest = PutDataMapRequest.create("/first_boss_health")
                putDataMapRequest.dataMap.apply {
                    putInt("firstBossHealth",firstBossHealth)
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
        }
    }

    // 웹소켓 - 실시간 러닝데이터 송신
    private fun updateRunDataSocket(
        distance: Double,
        runningTime: Long)
    {
        // 전송할 JSON 생성: {"runningData": {"distance": 5.2, "runningTime": 123}}
        val runningDataPayload = JSONObject().apply {
            put("distance", distance)
            put("runningTime", runningTime)
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
                val userId = responseJson.optString("userId", "unknown")
                val returnedDistance = responseJson.optDouble("distance", 0.0)
                val itemUseCount = responseJson.optInt("itemUseCount", 0)
                Log.d(
                    "Socket", "On - updateRunningData: " +
                            "userId: $userId, distance: $returnedDistance, itemUseCount: $itemUseCount"
                )

                // 워치에 보낼 playersData 에 위 정보 넣어서 그대로 보내기
                playersData = PlayersData(
                    userId = userId,
                    totalDistance = returnedDistance,
                    totalItemUsage = itemUseCount
                )

                // 업데이트된 playersData를 워치에 전송하기 위해 PutDataMapRequest 생성
                val putDataMapRequest = PutDataMapRequest.create("/players_data")
                putDataMapRequest.dataMap.apply {
                    putString("userId", userId)
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
        Log.d("Socket", "Emit - submitRunningResult: $runningResultJson")

        // updateRunningData 실시간 러닝 데이터 전송
        SocketHandler.mSocket.emit("submitRunningResult", runningResultJson)

        // 플레이어들 결과 공유 데이터 수신
        SocketHandler.mSocket.on("gameResult") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val responseJson = args[0] as JSONObject
                // 클리어/페일
                val cleared = responseJson.optBoolean("cleared", false)
                // 플레이어 결과 배열에 저장
                val playerResultsArray = responseJson.optJSONArray("playerResults")
                val playerResults = mutableListOf<PlayersResultData>()

                if (playerResultsArray != null) {
                    for (i in 0 until playerResultsArray.length()) {
                        val playerObj = playerResultsArray.optJSONObject(i)
                        playerObj?.let {
                            val result = PlayersResultData(
                                userId = it.optString("userId", "unknown"),
                                runningTime = it.optLong("runningTime", 0),
                                totalDistance = it.optDouble("totalDistance", 0.0),
                                paceAvg = it.optDouble("paceAvg", 0.0),
                                heartRateAvg = it.optDouble("heartRateAvg", 0.0),
                                cadenceAvg = it.optDouble("cadenceAvg", 0.0),
                                calories = it.optInt("calories", 0),
                                itemUseCount = it.optInt("itemUseCount", 0)
                            )
                            playerResults.add(result)
                        }
                    }
                }

                Log.d(
                    "Socket",
                    "On - gameResult: cleared: $cleared, playerResults: $playerResults"
                )
            }
        }
    }
}
