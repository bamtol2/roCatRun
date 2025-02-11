package com.eeos.rocatrun.result

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eeos.rocatrun.R
import com.eeos.rocatrun.game.GamePlay
import com.eeos.rocatrun.game.GifImage
import com.eeos.rocatrun.home.HomeActivity
import kotlinx.coroutines.delay

@Composable
fun SingleLoseScreen(playerResult: GamePlay.PlayersResultData?) {
    // Thunder GIF 표시 여부 상태
    var showThunder by remember { mutableStateOf(true) }

    val context = LocalContext.current

    // 모달이 시작되면 2초 뒤에 gif 숨김
    LaunchedEffect(Unit) {
        delay(2000L)
        showThunder = false
    }

    Dialog(
        onDismissRequest = { /**/ },
        // 백버튼이나 바깥화면 눌러도 무시
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(660.dp)
        ) {
            // 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.game_bg_resultmodal),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "다음 기회에",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontSize = 32.sp
                    )
                )
                Image(
                    painter = painterResource(id = R.drawable.game_img_losecat),
                    contentDescription = null,
                    modifier = Modifier
                        .size(130.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                ) {
                    FirstResultPage(playerResult = playerResult)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 확인 버튼
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFFFFFF00),
                            shape = RoundedCornerShape(7.dp)
                        )
                        .clickable {
                            // 홈화면으로 이동
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "확인",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        )
                    )
                }
            }

            // thunder GIF
            if (showThunder) {
                GifImage(
                    modifier = Modifier
                        .height(220.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 35.dp),
                    gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.game_gif_thunder}"
                )
            }
        }
    }
}

@Composable
private fun FirstResultPage(playerResult: GamePlay.PlayersResultData?) {
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
                .height(280.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),

                ) {
                // 2x2 그리드
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(23.dp)
                ) {
                    ResultItem("거리", "${playerResult?.totalDistance?.let { "%.1f".format(it) }}km")
                    ResultItem("시간", formatTime(playerResult?.runningTime ?: 0))
                }
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(23.dp)
                ) {
                    ResultItem("페이스", formatPace(playerResult?.paceAvg ?: 0.0))
                    ResultItem("칼로리", "${playerResult?.calories ?: 0}kcal")
                }

                Spacer(modifier = Modifier.height(15.dp))

                // 구분선
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFFFFF00))
                )

                Spacer(modifier = Modifier.height(15.dp))
                ResultRow("공격 횟수", "${playerResult?.itemUseCount ?: 0}번")
                Spacer(modifier = Modifier.height(10.dp))
                ResultRow("획득 경험치", "+${playerResult?.rewardExp ?: 0}exp")
                Spacer(modifier = Modifier.height(10.dp))
                ResultRow("획득 코인", "+${playerResult?.rewardCoin ?: 0}코인")
            }
        }
    }
}

@Composable
private fun ResultItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(text = ">>", color = Color(0xFFFFFF00), fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = value, color = Color.White, fontSize = 20.sp)
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.White)
        Text(text = value, color = Color.White)
    }
}

// 시간 포맷팅 함수
private fun formatTime(timeInMillis: Long): String {
    val hours = timeInMillis / (1000 * 60 * 60)
    val minutes = (timeInMillis % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (timeInMillis % (1000 * 60)) / 1000
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

// 페이스 포맷팅 함수
private fun formatPace(paceInMinutesPerKm: Double): String {
    val minutes = paceInMinutesPerKm.toInt()
    val seconds = ((paceInMinutesPerKm - minutes) * 60).toInt()
    return String.format("%02d'%02d\"", minutes, seconds)
}
