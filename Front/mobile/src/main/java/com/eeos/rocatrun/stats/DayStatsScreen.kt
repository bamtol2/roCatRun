package com.eeos.rocatrun.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.eeos.rocatrun.R
import androidx.compose.foundation.*
import androidx.compose.ui.graphics.painter.Painter

data class Player(
    val rank: Int,           // 순위
    val profileImage: Painter, // 프로필 이미지
    val name: String,        // 이름
    val distance: String,    // 거리
    val ammoCount: String    // 공격 횟수
)

@Composable
fun DayStatsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Stats cards
        DayStatCard(
            date = "2025/01/22",
            status = "정복완료",
            players = listOf(
                Player(
                    rank = 1,
                    profileImage = painterResource(id = R.drawable.stats_img_profile),
                    name = "과즙가람",
                    distance = "9.5km",
                    ammoCount = "3"
                ),
                Player(
                    rank = 2,
                    profileImage = painterResource(id = R.drawable.stats_img_profile),
                    name = "타노스",
                    distance = "5.5km",
                    ammoCount = "2"
                )
            ),
            isSuccess = true,
            bossImg = painterResource(id = R.drawable.all_img_boss1),
            onClick = {}
        )
        Spacer(modifier = Modifier.height(16.dp))
        DayStatCard(
            date = "2025/01/21",
            status = "정복실패",
            players = listOf(
                Player(
                    rank = 0,
                    profileImage = painterResource(id = R.drawable.stats_img_profile),
                    name = "과즙가람",
                    distance = "5.3km",
                    ammoCount = "3"
                ),
            ),
            isSuccess = false,
            bossImg = painterResource(id = R.drawable.all_img_boss2),
            onClick = {}
        )
        Spacer(modifier = Modifier.height(16.dp))
        DayStatCard(
            date = "2025/01/22",
            status = "정복완료",
            players = listOf(
                Player(
                    rank = 1,
                    profileImage = painterResource(id = R.drawable.stats_img_profile),
                    name = "과즙가람",
                    distance = "9.5km",
                    ammoCount = "3"
                ),
                Player(
                    rank = 2,
                    profileImage = painterResource(id = R.drawable.stats_img_profile),
                    name = "타노스",
                    distance = "5.5km",
                    ammoCount = "2"
                )
            ),
            isSuccess = true,
            bossImg = painterResource(id = R.drawable.all_img_boss1),
            onClick = {}
        )
    }
}

@Composable
fun DayStatCard(
    date: String,
    status: String,
    players: List<Player>,
    isSuccess: Boolean,
    bossImg: Painter,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.stats_bg_day),
            contentDescription = "Card Background",
            contentScale = ContentScale.FillWidth,
            alpha = 0.8f,
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = 30.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // 날짜 및 인원수
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${players.size}인",
                            color = Color.White,
                            fontSize = 24.sp
                        )

                        val imageRes = if (players.size == 1) {
                            R.drawable.stats_img_person
                        } else {
                            R.drawable.stats_img_people
                        }

                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Boss Img",
                            modifier = Modifier
                                .size(25.dp)
                                .offset(x = 5.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 게임 결과
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Image(
                        painter = bossImg,
                        contentDescription = "Boss Img",
                        modifier = Modifier.size(35.dp)
                    )

                    Text(
                        text = status,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSuccess) Color(0xFF36DBEB) else Color(0xFFA3A1A5),
                        modifier = Modifier.offset(x = 15.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 플레이어 정보
                Text(
                    text = "플레이어",
                    color = Color.White,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                players.forEachIndexed { index, player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x38FFFFFF), shape = RoundedCornerShape(10.dp))
                            .padding(top = 8.dp, bottom = 8.dp, start = 4.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val rankImage = when (player.rank) {
                            1 -> R.drawable.stats_img_first
                            2 -> R.drawable.stats_img_second
                            3 -> R.drawable.stats_img_third
                            else -> null
                        }

                        if (rankImage != null) {
                            Image(
                                painter = painterResource(id = rankImage),
                                contentDescription = "${player.rank}등 이미지",
                                modifier = Modifier.size(35.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(
                                text = "-",
                                color = Color.White,
                                fontSize = 14.sp,
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                        }

                        Image(
                            painter = player.profileImage,
                            contentDescription = "Profile Img",
                            modifier = Modifier
                                .size(35.dp)
                                .weight(0.5f)
                        )
                        Text(
                            text = player.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1.5f)
                        )
                        Text(
                            text = player.distance,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.stats_img_can),
                            contentDescription = "Can Img",
                            modifier = Modifier
                                .size(25.dp)
                                .weight(0.3f)
                        )
                        Text(
                            text = "x",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.2f)
                        )
                        Text(
                            text = player.ammoCount,
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
