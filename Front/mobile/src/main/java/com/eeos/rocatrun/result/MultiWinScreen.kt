package com.eeos.rocatrun.result

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eeos.rocatrun.R
import com.eeos.rocatrun.game.GamePlay
import com.eeos.rocatrun.ui.components.GifImage
import com.eeos.rocatrun.home.HomeActivity
import kotlinx.coroutines.delay
import com.eeos.rocatrun.ui.components.formatTime
import com.eeos.rocatrun.ui.components.formatPace

@Composable
fun MultiWinScreen(myResult: GamePlay.MyResultData?, myRank: Int, playerResults: List<GamePlay.PlayersResultData?>)
{
    // confetti GIF 표시 여부 상태
    var showConfetti by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // 모달이 시작되면 3초 후에 confetti 숨김
    LaunchedEffect(Unit) {
        delay(4000L)
        showConfetti = false
    }
    val pagerState = rememberPagerState(pageCount = {2})

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
                    text = "정복 완료",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontSize = 32.sp
                    )
                )
                Image(
                    painter = painterResource(id = R.drawable.game_img_wincat),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                )

                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(0.9f)
                        .border(width = 1.dp, color = Color(0xFFFFFF00))
                        .background(Color(0x7820200D))
                        .padding(16.dp)
//                    .height(240.dp),
                )
                {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()

                    ) {
                        // 게임 결과 정보 HorizontalPager
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) { page ->
                            when (page) {
                                0 -> FirstResultPage(myResult = myResult)
                                1 -> SecondResultPage(playerResults = playerResults)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 인디케이터
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(2) { index ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(8.dp)
                                        .background(
                                            if (pagerState.currentPage == index) Color.White else Color.Gray,
                                            CircleShape
                                        )
                                )
                            }
                        }
                    }
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
                            // 홈화면으로 이동.
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

            // confetti GIF
            if (showConfetti) {
                GifImage(
                    modifier = Modifier.width(360.dp),
                    gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.game_gif_confetti}"
                )
            }
        }

    }

}

@Composable
private fun FirstResultPage(myResult: GamePlay.MyResultData?) {

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            ) {
            // 2x2 그리드
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ResultItem("거리", "${myResult?.totalDistance?.let { "%.1f".format(it)}}km")
                ResultItem("시간", formatTime(myResult?.runningTime ?: 0))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(23.dp)
            ) {
                ResultItem("페이스", formatPace(myResult?.paceAvg ?: 0.0))
                ResultItem("칼로리", "${myResult?.calories ?: 0}kcal")
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
            ResultRow("공격 횟수", "${myResult?.itemUseCount ?: 0}번")
            Spacer(modifier = Modifier.height(10.dp))
            ResultRow("획득 경험치", "+${myResult?.rewardExp ?: 0}exp")
            Spacer(modifier = Modifier.height(10.dp))
            ResultRow("획득 코인", "+${myResult?.rewardCoin ?: 0}코인")
        }
    }
}


@Composable
private fun SecondResultPage(playerResults: List<GamePlay.PlayersResultData?>) {

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // 헤더
            Row {

                Text(text = "순위", color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(36.dp))
                Text(text = "닉네임", color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(35.dp))
                Text(text = "거리", color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(27.dp))
                Text(text = "보상", color = Color.White, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color(0xFFFFFF00))
            )

            Spacer(modifier = Modifier.height(4.dp))

            playerResults.forEachIndexed { index, player ->
                if (player != null) {
                    RankingWinRow(
                        rank = index + 1,
                        profileImage = player.characterImage, // 기본 이미지 사용
                        nickname = player.nickname,
                        distance = String.format("%.1fkm", player.totalDistance),
                        reward = "+${player.rewardExp}exp",
                        totalParticipants = playerResults.size
                    )
                } else {
                    Log.d("Socket", "Error - 데이터 파싱 에러")
                }
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

@Composable
private fun RankingWinRow(
    rank: Int,
    profileImage: String,
    nickname: String,
    distance: String,
    reward: String,
    totalParticipants: Int
) {
    val context = LocalContext.current
    val imageResId = getDrawableResourceId(context, profileImage)

    // 닉네임을 5자 이상일 때만 4자로 제한
    val displayNickname = if (nickname.length >= 5) {
        nickname.take(4)
    } else {
        nickname
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = Color(0x38FFFFFF),
                shape = RoundedCornerShape(size = 10.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1.2f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 순위 표시 (메달 이미지 또는 텍스트)
                Box(modifier = Modifier.width(24.dp)) {
                    when (rank) {
                        1 -> Image(
                            painter = painterResource(id = R.drawable.all_img_goldpaw),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        2 -> if (totalParticipants >= 2) Image(
                            painter = painterResource(id = R.drawable.all_img_silverpaw),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        3 -> if (totalParticipants >= 3) Image(
                            painter = painterResource(id = R.drawable.all_img_bronzepaw),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        else -> Text(text = "-", color = Color.White, fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.Center))
                    }
                }

                Spacer(modifier = Modifier.width(5.dp))
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = displayNickname,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

            }
            // 거리 (전체 너비의 25% 차지)
            Text(
                text = distance,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )

            // 보상 (전체 너비의 25% 차지)
            Text(
                text = reward,
                color = Color(0xFFFFDA0A),
                fontSize = 12.sp,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.End
            )
        }
    }
}

// 이미지 리소스 ID를 가져오는 함수
private fun getDrawableResourceId(context: Context, filename: String): Int {
    val resourceId = context.resources.getIdentifier(
        filename.substringBeforeLast("."), // 확장자 제거
        "drawable",
        context.packageName
    )
    // 리소스를 찾지 못하면 기본 이미지 반환
    return if (resourceId != 0) resourceId else R.drawable.game_img_wincat
}