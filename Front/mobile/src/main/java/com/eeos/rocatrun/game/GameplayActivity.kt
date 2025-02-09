package com.eeos.rocatrun.game

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.eeos.rocatrun.socket.SocketHandler
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.PutDataMapRequest
import org.json.JSONObject

class GamePlay : ComponentActivity(), DataClient.OnDataChangedListener {
    private lateinit var dataClient: DataClient
    private var runningData by mutableStateOf<RunningData?>(null)
    private var resultData by mutableStateOf<ResultData?>(null)
    private var playersData by mutableStateOf<PlayersData?>(null)
    
    // 실시간 러닝 데이터
    data class RunningData(
        val totalDistance: Double,
        val elapsedTime: Long,
        val itemUsed: Boolean,
    )
    
    // 결과 데이터
    data class ResultData(
        val totalDistance: Double,
        val elapsedTime: Long,
        val averagePace: Double,
        val averageHeartRate: Double,
        val totalItemUsage: Int,
    )

    // 실시간 팀원들 데이터
    data class PlayersData(
        val userId: String,
        val totalDistance: Double,
        val totalItemUsage: Int,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataClient = Wearable.getDataClient(this)
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
                    GameplayScreen()
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
//                    "/gpx_data" -> processGpxData(dataItem)
                }
            }
        }
    }

    private fun processRunningData(dataItem: DataItem) {
        DataMapItem.fromDataItem(dataItem).dataMap.apply {
            runningData = RunningData(
                totalDistance = getDouble("distance"),
                elapsedTime = getLong("time"),
                itemUsed = getBoolean("itemUsed")
            )
        }

        Log.d("Wear","러닝 데이터 받는중 - $runningData")

        // 웹소켓으로 전송
        runningData?.let { data ->
            updateRunDataSocket(data.totalDistance, data.elapsedTime)
        }
    }

    private fun processResultData(dataItem: DataItem) {
        DataMapItem.fromDataItem(dataItem).dataMap.apply {
            resultData = ResultData(
                totalDistance = getDouble("distance"),
                elapsedTime = getLong("time"),
                averagePace = getDouble("averagePace"),
                averageHeartRate = getDouble("averageHeartRate"),
                totalItemUsage = getInt("totalItemUsage")
            )
        }

        Log.d("Wear","게임 최종 결과 받음 - $resultData")

        // rest api 송신 보낼거임
        // 결과 모달에도 나타내줘야됨
    }

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
}