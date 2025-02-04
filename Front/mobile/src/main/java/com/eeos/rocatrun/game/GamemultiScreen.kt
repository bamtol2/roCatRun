package com.eeos.rocatrun.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun GamemultiScreen(runningData: GameMulti.RunningData?) {

    // 타이머 상태 관리
    var seconds by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // 타이머 로직
    LaunchedEffect(key1 = isTimerRunning) {
        while(isTimerRunning && seconds < 1800) {
            // 30분 = 1800초
            delay(1000L) // 1초 대기
            seconds++ // 1초씩 증가
        }
    }

    // 시간 포맷팅 함수
    fun formatTime(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val remainingSeconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }



    Box(modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.game_bg_gameroom),
            contentDescription = "multigame play page background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 100.dp),  // 상단 여백 추가
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 30분 타이머
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // 배경 이미지
                Image(
                    painter = painterResource(id = R.drawable.game_img_timer),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(45.dp)
                        .width(120.dp)
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Image(
                        painter = painterResource(id = R.drawable.game_icon_timer),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatTime(seconds),
                        color = Color.White,
                        fontSize = 22.sp
                    )
                }

            }

            Spacer(modifier = Modifier.height(80.dp))

            // 보스 게이지
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.game_img_monster1),
                    contentDescription = "Boss",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BOSS",
                            color = Color.White,
                            fontSize = 30.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .background(Color.Red)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 나의 아이템 게이지
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly  // 요소들을 양끝으로 정렬
            ) {
                Column(
                    modifier = Modifier.weight(1f)  // 남은 공간을 모두 차지하도록 설정
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ITEM",
                            color = Color.White,
                            fontSize = 30.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .background(Color.Green)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.all_img_whitecat),
                    contentDescription = "my character",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            // 현재 상태 정보
            CurrentInfo(runningData)
        }
    }
}

// 현재 상태 출력 박스
@Composable
private fun CurrentInfo(runningData: GameMulti.RunningData?) {

    fun formatTime(milliseconds: Long): String {
        val totalSeconds = (milliseconds / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
    fun formatPace(pace: Double): String {
        // 예: 5.5 -> "5'30""
        val minutes = pace.toInt()
        val seconds = ((pace - minutes) * 60).roundToInt()
        return "$minutes'${"%02d".format(seconds)}\""
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(width = 1.dp, color = Color(0xFFFFFF00))
                .background(Color(0x7820200D))
                .padding(16.dp)
                .height(240.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 40.dp),

                ) {
                // 2x2 그리드
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(23.dp)
                ) {
                    val distanceText = runningData?.totalDistance?.let { "${"%.2f".format(it)} km" } ?: "N/A"
                    val timeText = runningData?.elapsedTime?.let { formatTime(it) } ?: "N/A"
                    ResultItem("거리", distanceText)
                    ResultItem("시간", timeText)
                }
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(23.dp)
                ) {
                    val paceText = runningData?.averagePace?.let { formatPace(it) } ?: "N/A"
                    val heartText = runningData?.heartRate ?: "N/A"
                    ResultItem("페이스", paceText)
                    ResultItem("심박수", heartText)
                }

                Spacer(modifier = Modifier.height(15.dp))

            }
        }
    }
}

@Composable
private fun ResultItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(text = ">>", color = Color(0xFFFFFF00), fontSize = 25.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = value, color = Color.White, fontSize = 25.sp)
        }
    }
}



