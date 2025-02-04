package com.eeos.rocatrun.game


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
import com.eeos.rocatrun.home.HomeActivity

@Composable
fun MultiLoseScreen() {

    val pagerState = rememberPagerState(pageCount = {2})
    val context = LocalContext.current

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

                // 게임 결과 정보 HorizontalPager

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                ) { page ->
                    when (page) {
                        0 -> FirstResultPage()
                        1 -> SecondResultPage()
                    }
                }

                // 인디케이터
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(8.dp))

                // 입장 버튼
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFFFFFF00),
                            shape = RoundedCornerShape(7.dp)
                        )
                        // 입장 클릭하면 대기중 화면 띄우기
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
        }
    }
}

@Composable
private fun FirstResultPage() {
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
                    .padding(horizontal = 10.dp, vertical = 10.dp),

                ) {
                // 2x2 그리드
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(23.dp)
                ) {
                    ResultItem("거리", "42.5km")
                    ResultItem("시간", "00:20:30")
                }
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(23.dp)
                ) {
                    ResultItem("페이스", "06'32\"")
                    ResultItem("칼로리", "320kcal")
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
                ResultRow("순위", "1위")
                Spacer(modifier = Modifier.height(10.dp))
                ResultRow("획득 경험치", "+10exp")
            }
        }
    }
}

@Composable
private fun SecondResultPage() {

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(width = 1.dp, color = Color(0xFFFFFF00))
                .background(Color(0x7820200D))
                .padding(16.dp)
                .height(240.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 헤더
                Row {

                    Text(text = "순위", color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(110.dp))
                    Text(text = "거리", color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(30.dp))
                    Text(text = "보상", color = Color.White, fontSize = 16.sp)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFFFFF00))
                )

                // 참가자 목록 (예시로 3명으로 설정, 추후 연동해서 수정할거임)
                val participants = listOf(
                    Triple("타노스", "42.5km", "+10exp"),
                    Triple("마이애미", "40.2km", "+8exp"),
                    Triple("과즙가람", "38.7km", "+6exp"),
                    Triple("규리몽땅", "31.7km", "+20exp")
                )

                participants.forEachIndexed { index, (nickname, distance, reward) ->
                    RankingRow(
                        rank = index + 1,
                        profileImage = R.drawable.game_img_losecat,
                        nickname = nickname,
                        distance = distance,
                        reward = reward,
                        totalParticipants = participants.size
                    )
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
private fun RankingRow(
    rank: Int,
    profileImage: Int,
    nickname: String,
    distance: String,
    reward: String,
    totalParticipants: Int
) {
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
                .padding(horizontal = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1.4f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 순위 표시 (메달 이미지 또는 텍스트)
                Box(modifier = Modifier.width(24.dp)) {
                     Text(text = "-", color = Color.White, fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.Center))
                }


                Spacer(modifier = Modifier.width(5.dp))
                Image(
                    painter = painterResource(id = profileImage),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = nickname,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

            }
            // 거리 (전체 너비의 25% 차지)
            Text(
                text = distance,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )

            // 보상 (전체 너비의 25% 차지)
            Text(
                text = reward,
                color = Color(0xFFFFDA0A),
                fontSize = 14.sp,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.End
            )
        }
    }
}
