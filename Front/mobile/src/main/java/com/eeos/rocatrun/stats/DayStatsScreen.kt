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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.eeos.rocatrun.R
import androidx.compose.foundation.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.eeos.rocatrun.stats.api.Game
import com.eeos.rocatrun.stats.api.Player
import com.eeos.rocatrun.ui.components.StrokedText
import com.eeos.rocatrun.ui.theme.MyFontFamily


@Composable
fun DayStatsScreen(games: List<Game>?, noDayData: Boolean) {
    // 세부 모달을 위한 상태: 클릭한 게임의 데이터를 저장
    var selectedGame by remember { mutableStateOf<Pair<Game, Int>?>(null) }

    if (noDayData) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "데이터가 없다냥!\n달리기를 시작하라냥..!",
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            games?.forEachIndexed {index, game ->
                DayStatCard(
                    date = game.date,
                    status = if (game.result) "정복완료" else "정복실패",
                    players = game.players.map { player ->
                        Player(
                            rank = player.rank,
                            profileUrl = player.profileUrl,
                            nickname = player.nickname,
                            distance = player.distance,
                            attackCount = player.attackCount
                        )
                    },
                    isSuccess = game.result,
                    difficulty = game.difficulty,
                    onClick = { selectedGame = Pair(game,index) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 세부 모달 표시: selectedGame이 null이 아닐 때만 보여줌
    selectedGame?.let { (game,index) ->
        DetailDialog(
            date = game.date,
            details = game.details,  // game.details 전달
            recordIndex = index,
            onDismiss = { selectedGame = null }
        )
    }
}

@Composable
fun DayStatCard(
    date: String,
    status: String,
    players: List<Player>,
    isSuccess: Boolean,
    difficulty: String,
    onClick: () -> Unit,
) {
    val dateWithoutTime = date.substringBefore("T").replace("-", "/")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // 배경 이미지
        val bgImage = when (players.size) {
            1 -> R.drawable.stats_bg_day_one
            2 -> R.drawable.stats_bg_day_two
            3 -> R.drawable.stats_bg_day_three
            4 -> R.drawable.stats_bg_day_four
            else -> null
        }

        if (bgImage != null) {
            Image(
                painter = painterResource(id = bgImage),
                contentDescription = "Card Background",
                contentScale = ContentScale.Crop,
                alpha = 0.8f,
                modifier = Modifier.fillMaxWidth()
            )
        }


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
                    StrokedText(
                        text = dateWithoutTime,
                        color = Color.White,
                        strokeColor = Color.Black,
                        fontSize = 25,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StrokedText(
                            text = "${players.size}인",
                            color = Color.White,
                            strokeColor = Color.Black,
                            fontSize = 25,
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val bossImg = when (difficulty) {
                        "HARD" -> R.drawable.all_img_boss1
                        "NORMAL" -> R.drawable.all_img_boss2
                        "EASY" -> R.drawable.all_img_boss3
                        else -> null
                    }

                    if (bossImg != null) {
                        Image(
                            painter = painterResource(id = bossImg),
                            contentDescription = "Boss Img",
                            modifier = Modifier.size(35.dp)
                        )
                    }

                    StrokedText(
                        text = status,
                        color = if (isSuccess) Color(0xFF36DBEB) else Color(0xFFA3A1A5),
                        strokeColor = Color.Black,
                        fontSize = 34,
                        modifier = Modifier.offset(x = 15.dp)
                    )

                    // 세부 모달 버튼
                    Button(
                        onClick = onClick,
                        modifier = Modifier
                            .offset(x = 84.dp)
                            .border(2.dp, Color(0xFF2EB5DC), RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                        contentPadding = PaddingValues(horizontal = 7.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "개인 기록",
                            style = TextStyle(
                                fontFamily = MyFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 플레이어 정보
                StrokedText(
                    text = "플레이어",
                    color = Color.White,
                    strokeColor = Color.Black,
                    fontSize = 20,
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
                            1 -> R.drawable.all_img_goldpaw
                            2 -> R.drawable.all_img_silverpaw
                            3 -> R.drawable.all_img_bronzepaw
                            else -> null
                        }

                        if (status == "정복완료" && rankImage != null) {

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


                        val imageUrl = if (player.profileUrl == "default.png") {
                            "android.resource://com.eeos.rocatrun/${R.drawable.all_img_whitecat}" // 기본 이미지로 교체
                        } else {
                            player.profileUrl
                        }

                        Image(
                            painter = coil.compose.rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Profile Img",
                            modifier = Modifier
                                .size(35.dp)
                                .weight(0.5f)
                        )
                        Text(
                            text = player.nickname,
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1.5f)
                        )
                        Text(
                            text = "${roundToFirstDecimal(player.distance)}km",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(0.5f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.all_img_fishbone),
                            contentDescription = "fishbone Img",
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
                            text = player.attackCount.toString(),
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.2f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
