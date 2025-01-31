package com.eeos.rocatrun.game

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R

@Composable
fun LoadingScreen(
    // 임시로 넣어둠. 연동할 때 바꿀 예정
    currentUsers: Int = 3,   // 현재 접속한 사용자 수
    maxUsers: Int = 4        // 방 정원
) {
    val context = LocalContext.current

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
                .padding(horizontal = 25.dp, vertical = 250.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 매칭 중 박스
            Box {
                Image(
                    painter = painterResource(id = R.drawable.game_img_lightpink),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.FillBounds
                )

                // stroke 먹인 글씨,,
                Text(
                    text = "대기 중...",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFFF20089),
                        fontSize = 45.sp,
                        drawStyle = Stroke(
                            width = 10f,
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
                        fontSize = 45.sp,
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
                                    width = 18f,
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
