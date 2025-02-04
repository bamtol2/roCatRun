package com.eeos.rocatrun.home

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.game.GameRoom
import com.eeos.rocatrun.profile.ProfileDialog
import com.eeos.rocatrun.ranking.RankingDialog
import com.eeos.rocatrun.stats.StatsActivity
import com.eeos.rocatrun.ui.theme.MyFontFamily


@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // 랭킹 모달 변수
    var showRanking by remember { mutableStateOf(false) }

    // 프로필 모달 변수
    var showProfile by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.home_bg_image),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .offset(x = 0.dp, y = 47.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // 왼쪽 상단 버튼 (랭킹)
            Button(
                modifier = Modifier
                    .align(Alignment.TopStart),
                onClick = { showRanking = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_icon_ranking),
                    contentDescription = "Ranking Icon",
                    modifier = Modifier.size(70.dp)
                )
            }

            // 오른쪽 상단 세로 버튼들 (프로필, 통계, 옷장)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd),
            ) {
                Button(
                    onClick = { showProfile = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.home_icon_profile),
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(70.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { context.startActivity(Intent(context, StatsActivity::class.java)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.home_icon_stats),
                        contentDescription = "Statistics Icon",
                        modifier = Modifier.size(70.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* 옷장 화면으로 전환 */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.home_icon_closet),
                        contentDescription = "Closet Icon",
                        modifier = Modifier.size(70.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = 0.dp, y = 290.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 캐릭터 이미지
            Image(
                painter = painterResource(id = R.drawable.all_img_whitecat),
                contentDescription = "Cat Character",
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 레벨 표시
            Text(
                text = "Lv. 1",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 레벨 게이지
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f) // 현재 40% 진행
                        .height(8.dp)
                        .background(Color.Green)
                )
            }
            Spacer(modifier = Modifier.height(55.dp))

            // START 버튼
            Button(
                onClick = { context.startActivity(Intent(context, GameRoom::class.java)) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(0.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .width(216.dp)
                    .height(72.dp)
            ) {
                Box() {
                    // 배경 이미지
                    Image(
                        painter = painterResource(id = R.drawable.home_btn_start),
                        contentDescription = "Start Button",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    //Filled Text
                    Text(
                        text = "START",
                        color = Color(0xFFFFFFFF),
                        fontSize = 42.86.sp,
                        fontFamily = MyFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                    //Text with stroke border
                    Text(
                        text = "START",
                        color = Color(0xff36DBEB),
                        style = TextStyle.Default.copy(
                            fontSize = 42.86.sp,
                            drawStyle = Stroke(
                                miter = 10f,
                                width = 5f,
                                join = StrokeJoin.Round
                            )
                        ),
                        fontFamily = MyFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )

                }
            }
        }


        // 랭킹 모달 표시
        if (showRanking) {
            RankingDialog(onDismiss = { showRanking = false })
        }

        // 프로필 모달 표시
        if (showProfile) {
            ProfileDialog(onDismiss = { showProfile = false })
        }

    }
}
