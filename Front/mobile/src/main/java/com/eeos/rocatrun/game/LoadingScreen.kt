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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.eeos.rocatrun.R
import com.eeos.rocatrun.socket.SocketHandler
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import org.json.JSONObject

@Composable
fun LoadingScreen(
    generatedCode: String? = null,
    initialCurrentUsers: Int = 0,   // 현재 접속한 사용자 수
    initialMaxUsers: Int = 0       // 방 정원
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var currentUsers by remember { mutableStateOf(initialCurrentUsers) }
    var maxUsers by remember { mutableStateOf(initialMaxUsers) }
    val dataClient = Wearable.getDataClient(context)

    LaunchedEffect(Unit) {
//        SocketHandler.mSocket.off("playerJoined") // 기존 리스너 제거
        SocketHandler.mSocket.on("playerJoined") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val json = args[0] as JSONObject
                val userId = json.optString("userId", "")
                val currentPlayers = json.optInt("currentPlayers", 0)
                val maxPlayers = json.optInt("maxPlayers", 0)
                Log.d(
                    "Socket",
                    "On - playerJoined: userId: $userId, currentPlayers: $currentPlayers, maxPlayers: $maxPlayers"
                )
                // 갱신된 값을 상태에 반영
                currentUsers = json.optInt("currentPlayers", currentUsers)
                maxUsers = json.optInt("maxPlayers", maxUsers)
            }
        }
        SocketHandler.mSocket.on("playerLeft") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val json = args[0] as JSONObject
                val userId = json.optString("userId", "")
                val currentPlayers = json.optInt("currentPlayers", 0)
                val maxPlayers = json.optInt("maxPlayers", 0)
                Log.d(
                    "Socket",
                    "On - playerLeft: userId: $userId, currentPlayers: $currentPlayers, maxPlayers: $maxPlayers"
                )
                // 갱신된 값을 상태에 반영
                currentUsers = json.optInt("currentPlayers", currentUsers)
                maxUsers = json.optInt("maxPlayers", maxUsers)
            }
        }
        SocketHandler.mSocket.on("gameReady") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val json = args[0] as JSONObject
                val message = json.optString("message", "")

                Log.d("Socket", "On - gameReady $message")

                // GameplayActivity로 이동
                val intent = Intent(context, GamePlay::class.java)
                context.startActivity(intent)
            }

        }

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

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.game_bg_loading),
            contentDescription = "loading page background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            // 큰 박스
            Box(
                modifier = Modifier
                    .padding(start = 50.dp)  // 이미지가 왼쪽으로 나갈 공간 확보
                    .align(Alignment.Start)
            ) {
                // 고스트 이미지 (앞으로 나오도록 zIndex 설정)
                Image(
                    painter = painterResource(id = R.drawable.game_img_ghost),
                    contentDescription = "Ghost character",
                    modifier = Modifier
                        .size(70.dp)
                        .offset(x = (-50).dp, y = 42.dp)  // 왼쪽으로 이미지 이동
                        .zIndex(1f)  // 이미지가 박스 위로 오도록 설정
                )


                // 초대코드 박스
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF17204D),
                            shape = RoundedCornerShape(10.dp)
                        )
//                        .border(width = 2.dp, color = Color(0xFF2D4FFF),shape = RoundedCornerShape(size = 10.dp))
                        .width(230.dp)
                        .height(110.dp)
                        .padding(horizontal = 5.dp, vertical = 5.dp)
                        .zIndex(0f)  // 박스는 이미지 뒤로
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .align(Alignment.Center)

                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Start))
                        {
                            Text(
                                text = "초대코드는",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color(0xFF596CCE),
                                    fontSize = 15.sp,
                                    drawStyle = Stroke(
                                        width = 10f,
                                        join = StrokeJoin.Round
                                    ))
                            )
                            Text(
                                text = "초대코드는",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 15.sp)

                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 코드박스
                        Row(
                            modifier = Modifier.fillMaxWidth(),  // Row가 전체 너비를 차지하도록 함
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = generatedCode ?: "",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Image(
                                painter = painterResource(id = R.drawable.game_icon_copy),
                                contentDescription = "Copy",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        generatedCode?.let {
                                            clipboardManager.setText(AnnotatedString(it))
                                            Toast.makeText(
                                                context,
                                                "코드가 복사되었습니다",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 4.dp))
                        {
                            Text(
                                text = "여기있다냥",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color(0xFF596CCE),
                                    fontSize = 15.sp,
                                    drawStyle = Stroke(
                                        width = 10f,
                                        join = StrokeJoin.Round
                                    ))
                            )

                            Text(
                                text = "여기있다냥",
                                color = Color.White,
                                fontSize = 15.sp,

                            )
                        }
                    }
                }
            }

            // 큰 박스와 대기 중 박스 사이 간격
            Spacer(modifier = Modifier.height(40.dp))

            // 대기 중 박스
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.game_img_lightpink),
                    contentDescription = null,
                    modifier = Modifier
                        .width(300.dp)
                        .height(100.dp),
                    contentScale = ContentScale.FillBounds
                )

                // stroke 먹인 글씨,,
                Text(
                    text = "대기 중...",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFFF20089),
                        fontSize = 40.sp,
                        drawStyle = Stroke(
                            width = 13f,
                            join = StrokeJoin.Round
                        )
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(
                    text = "대기 중...",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontSize = 40.sp,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // 인원 표시
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.game_icon_users),
                    contentDescription = "people icon",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))

                // 인원 수 표시 부분을 Box로 감싸서 텍스트 겹치기
                Box {
                    Text(
                        text = "$currentUsers/$maxUsers",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(0xFFF20089),
                            fontSize = 55.sp,
                            letterSpacing = 5.sp,
                            drawStyle = Stroke(
                                width = 14f,
                                join = StrokeJoin.Round
                            )
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Text(
                        text = "$currentUsers/$maxUsers",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontSize = 55.sp,
                            letterSpacing = 5.sp,
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

            // 취소 버튼 -> 누르면 게임선택 창으로 돌아가게
            Box(
                modifier = Modifier.clickable {

                    // 매칭취소 이벤트 발생
                    SocketHandler.mSocket.emit("cancelMatch")

                    val intent = Intent(context, GameRoom::class.java)
                    context.startActivity(intent)
                }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 취소 텍스트
                    Box {
                        Text(
                            text = "취소",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color(0xA8F20089),
                                fontSize = 35.sp,
                                drawStyle = Stroke(
                                    width = 20f,
                                    join = StrokeJoin.Round
                                ),
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Text(
                            text = "취소",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontSize = 35.sp,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
