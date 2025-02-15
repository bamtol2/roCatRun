package com.eeos.rocatrun.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eeos.rocatrun.R

@Composable
fun BossScreen(
    onDismissRequest: () -> Unit
){
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(700.dp)
                .border(
                    width = 3.dp,
                    color = Color(0xFF00E2B1)
                )
                .background(color = Color(0xB2000000))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                // 헤더
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF00E2B1))
                        .height(50.dp)

                ) {
                    // 닫기 아이콘
                    Image(
                        painter = painterResource(id = R.drawable.game_icon_close),
                        contentDescription = "Close",
                        modifier = Modifier
                            .align(Alignment.CenterStart)  // 왼쪽 중앙 정렬
                            .padding(start = 10.dp)
                            .size(32.dp)
                            .clickable { onDismissRequest() }
                    )

                    // 보스 정보 텍스트
                    Text(
                        text = "보스 정보",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.Black,
                            fontSize = 32.sp,
                        ),
                        modifier = Modifier.align(Alignment.Center),  // 박스 중앙 정렬
                        textAlign = TextAlign.Center
                    )
                }

                // 보스 정보 내용
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .weight(1f)  // 남은 공간 모두 차지
                        .verticalScroll(rememberScrollState())  // 스크롤 가능하도록 설정
                        .padding(30.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    // 상
                    BossInfoSection(
                        difficulty = "상",
                        bossName = "나일론 마스크",
                        bossImage = R.drawable.all_img_boss1,
                        details = listOf(
                            "30분",   // 제한시간
                            "인당 6km",  // 거리
                            "160 ~ 1200", // 보상
                            "모든 유저가 공격 2회시", // 피버조건
                            "70 ~ 525" // 아이템 획득조건
                        )
                    )

                    // 중
                    BossInfoSection(
                        difficulty = "중",
                        bossName = "땅콩수집 로봇",
                        bossImage = R.drawable.all_img_boss2,
                        details = listOf(
                            "30분",   // 제한시간
                            "인당 5km",  // 거리
                            "100 ~ 750", // 보상
                            "모든 유저가 공격 2회시", // 피버조건
                            "40 ~ 300" // 아이템 획득조건
                        )
                    )

                    // 하
                    BossInfoSection(
                        difficulty = "하",
                        bossName = "사채업자 해파리",
                        bossImage = R.drawable.all_img_boss3,
                        details = listOf(
                            "30분",   // 제한시간
                            "인당 4km",  // 거리
                            "60 ~ 450", // 보상
                            "모든 유저가 공격 2회시", // 피버조건
                            "20 ~ 150" // 아이템 획득조건
                        )
                    )
                }
            }
        }
    }
}

// 보스 정보 박스
@Composable
private fun BossInfoSection(
    difficulty: String,
    bossName: String,
    bossImage: Int,
    details: List<String>
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 보스 정보 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)  // 난이도 텍스트와 겹치도록 상단 패딩 조정
                .background(Color(0x7D539086), shape = RoundedCornerShape(size = 10.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
//
                ) {
                    // 왼쪽 열: 보스 이미지와 이름
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                    ) {
                        Image(
                            painter = painterResource(id = bossImage),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Box{
                            // 보스이름 stroke 텍스트
                            Text(
                                text = bossName,
                                color = Color(0xFF0A7961),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 18.sp,
                                    drawStyle = Stroke(
                                        width = 10f,
                                        join = StrokeJoin.Round
                                    )
                                )
                            )
                            Text(
                                text = bossName,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }

                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 오른쪽 열: 제한시간, 거리, 보상
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StrokedText(text = "제한시간", fontSize = 17)
                        Text(text = details[0], color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        StrokedText(text = "거리", fontSize = 17)
                        Text(text = details[1], color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        StrokedText(text = "경험치(exp)", fontSize = 17)
                        Text(text = details[2], color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // 하단정보: 피버조건, 아이템 획득조건
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StrokedText(text = "피버조건", fontSize = 16)
                    Text(text = details[3], color = Color.White)
                    Spacer(modifier = Modifier.height(9.dp))
                    StrokedText(text = "캔코인 획득 범위", fontSize = 16)
                    Text(text = details[4], color = Color.White)
                }
            }
        }

        // 난이도 텍스트
        Text(
            text = difficulty,
            color = Color(0xFF00E2B1),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 35.sp,
                drawStyle = Stroke(
                    width = 20f,
                    join = StrokeJoin.Round
                )
            ),
            modifier = Modifier.padding(start = 10.dp)
        )
        Text(
            text = difficulty,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color(0xFF000000),
                fontSize = 35.sp
            ),
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

// 스트로크 글씨 함수
@Composable
fun StrokedText(
    text: String,
    fontSize: Int,
    strokeWidth: Float = 10f,
    color: Color = Color.White,
    strokeColor: Color = Color.Black
) {
    Box {
        // Stroke 텍스트
        Text(
            text = text,
            color = strokeColor,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = fontSize.sp,
                drawStyle = Stroke(
                    width = strokeWidth,
                    join = StrokeJoin.Round
                )
            )
        )
        // 일반 텍스트
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                color = color,
                fontSize = fontSize.sp
            )
        )
    }
}

