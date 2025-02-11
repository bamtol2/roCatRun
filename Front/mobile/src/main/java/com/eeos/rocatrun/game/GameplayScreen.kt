package com.eeos.rocatrun.game

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eeos.rocatrun.R
import com.eeos.rocatrun.game.GamePlay.PlayersResultData
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.result.MultiLoseScreen
import com.eeos.rocatrun.result.MultiWinScreen
import com.eeos.rocatrun.result.SingleLoseScreen
import com.eeos.rocatrun.result.SingleWinScreen
import com.eeos.rocatrun.socket.SocketHandler
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import org.json.JSONObject


@Composable
fun GameplayScreen(gpxFileReceived: Boolean, onShareClick: () -> Unit) {

    // 임시 테스트용 버튼
    var showMultiWinDialog by remember { mutableStateOf(false) }
    var showMultiLoseDialog by remember { mutableStateOf(false) }
    var showSingleWinDialog by remember { mutableStateOf(false) }
    var showSingleLoseDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dataClient = Wearable.getDataClient(context)

    LaunchedEffect(Unit) {

        // 웹소켓 - 보스체력 이벤트 수신 -> 워치 송신
        SocketHandler.mSocket.on("gameStatusUpdated") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val responseJson = args[0] as JSONObject
                val bossHealth = responseJson.optInt("bossHealth", 10000)

                Log.d(
                    "Socket", "On - gameStatusUpdated: " +
                            "bossHealth: $bossHealth"
                )

                // 워치에 bossHealth 보내기
                val putDataMapRequest = PutDataMapRequest.create("/boss_health")
                putDataMapRequest.dataMap.apply {
                    putInt("bossHealth",bossHealth)
                }
                val request = putDataMapRequest.asPutDataRequest().setUrgent()
                dataClient.putDataItem(request)
                    .addOnSuccessListener { _ ->
                        Log.d("Wear", "보스체력 업데이트 송신")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Wear", "보스체력 업데이트 실패", exception)
                    }
            }

        }

        // 웹소켓 - 피버시작 이벤트 수신 -> 워치 송신
        SocketHandler.mSocket.on("feverTimeStarted") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val responseJson = args[0] as JSONObject
                val active = responseJson.optBoolean("active", false)
                val duration = responseJson.optInt("duration", 0)

                Log.d(
                    "Socket", "On - feverTimeStarted"
                )

                // 워치에 피버타임 시작 메세지 보내기
                val putDataMapRequest = PutDataMapRequest.create("/fever_start")
                putDataMapRequest.dataMap.apply {
                    putBoolean("feverStart", true)
                    putLong("timestamp", System.currentTimeMillis())

                }
                val request = putDataMapRequest.asPutDataRequest().setUrgent()
                dataClient.putDataItem(request)
                    .addOnSuccessListener { _ ->
                        Log.d("Wear", "피버타임 시작")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Wear", "피버타임 시작 실패", exception)
                    }
            }
        }

        // 웹소켓 - 피버종료 이벤트 수신 -> 워치 송신
        SocketHandler.mSocket.on("feverTimeEnded") {
            Log.d("Socket", "On - feverTimeEnded")

            // 워치에 피버타임 종료 메세지 보내기
            val putDataMapRequest = PutDataMapRequest.create("/fever_end")
            putDataMapRequest.dataMap.apply {
                putBoolean("feverEnd", true)
                putLong("timestamp", System.currentTimeMillis())
            }
            val request = putDataMapRequest.asPutDataRequest().setUrgent()
            dataClient.putDataItem(request)
                .addOnSuccessListener { _ ->
                    Log.d("Wear", "피버타임 종료")
                }
                .addOnFailureListener { exception ->
                    Log.e("Wear", "피버타임 종료 실패", exception)
                }
        }

        // 플레이어들 결과 공유 데이터 수신 -> 워치 송신
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

        // 웹소켓 - 게임종료 이벤트 수신 -> 워치 송신
        SocketHandler.mSocket.on("gameOver") {
            Log.d("Socket", "On - gameOver")

            // 워치에 게임 종료 메세지 보내기
            val putDataMapRequest = PutDataMapRequest.create("/game_end")
            putDataMapRequest.dataMap.apply {
                putBoolean("gameEnd", true)
                putLong("timestamp", System.currentTimeMillis())
            }
            val request = putDataMapRequest.asPutDataRequest().setUrgent()
            dataClient.putDataItem(request)
                .addOnSuccessListener { _ ->
                    Log.d("Wear", "게임 종료")
                }
                .addOnFailureListener { exception ->
                    Log.e("Wear", "게임 종료 실패", exception)
                }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.game_bg_gameplay),
            contentDescription = "game play page background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 200.dp),    // 원하는 만큼 상단 패딩 조절
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {

            GifImage(modifier = Modifier.size(100.dp),
                gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.game_gif_greencan}"
            )

            Text(
                text = "게임 중입니다..",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontSize = 45.sp,
                ),
                textAlign = TextAlign.Center,
            )

            Text(
                text = "워치 화면을 확인해 주세요",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontSize = 20.sp,
                ),
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "홈으로 이동",
                    color = Color.White,
                    fontSize = 30.sp,
                    modifier = Modifier.clickable {
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }


            // 임시 테스트용 버튼
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "멀티 승리",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.clickable { showMultiWinDialog = true }
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "멀티 패배",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.clickable { showMultiLoseDialog = true }
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "싱글 승리",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.clickable { showSingleWinDialog = true }
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "싱글 패배",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.clickable { showSingleLoseDialog = true }
                        )
                    }
                }
            }
        }

        if (gpxFileReceived) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Green)
                    .padding(8.dp)
            ) {
                Text("GPX 파일 수신 완료", color = Color.White)
            }

            Button(
                onClick = onShareClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text("GPX 파일 공유")
            }
        }

        // 모달 표시
        if (showMultiWinDialog) {
            MultiWinScreen()
        }
        else if (showMultiLoseDialog) {
            MultiLoseScreen()
        }
        else if (showSingleWinDialog) {
            SingleWinScreen()
        }
        else if (showSingleLoseDialog) {
            SingleLoseScreen()
        }
    }
}

// gif 불러오는 함수
@Composable
fun GifImage(modifier: Modifier = Modifier, gifUrl: String) {

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(gifUrl)
            .crossfade(false)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = "GIF Image",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}