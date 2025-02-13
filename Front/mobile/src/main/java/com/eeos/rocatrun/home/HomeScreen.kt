package com.eeos.rocatrun.home

import android.content.Intent
import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.eeos.rocatrun.R
import com.eeos.rocatrun.game.GameRoom
import com.eeos.rocatrun.home.api.HomeViewModel
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.profile.ProfileDialog
import com.eeos.rocatrun.profile.api.ProfileViewModel
import com.eeos.rocatrun.ranking.RankingDialog
import com.eeos.rocatrun.ranking.api.RankingViewModel
import com.eeos.rocatrun.stats.StatsActivity
import com.eeos.rocatrun.ui.components.StrokedText


@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    val context = LocalContext.current
    val token = TokenStorage.getAccessToken(context)

    // ViewModel에서 가져온 데이터
    val homeInfoData = homeViewModel.homeData.observeAsState()

    // 프로필 관련 변수, 프로필 데이터가 없을 때만 호출
    val profileViewModel: ProfileViewModel = viewModel()
    var showProfile by remember { mutableStateOf(false) }
    val profileData by profileViewModel.profileData.observeAsState()

    LaunchedEffect(profileData) {
        if (profileData == null) {
            profileViewModel.fetchProfileInfo(token)
        }
    }

    // 랭킹 모달 변수, 랭킹 데이터가 없을 때만 호출
    val rankingViewModel: RankingViewModel = viewModel()
    var showRanking by remember { mutableStateOf(false) }
    val rankingData by rankingViewModel.rankingData.observeAsState()

    LaunchedEffect(rankingData) {
        if (rankingData == null) {
            rankingViewModel.fetchRankingInfo(token)
        }
    }

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
                    modifier = Modifier.size(60.dp)
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
                        modifier = Modifier.size(60.dp)
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
                        modifier = Modifier.size(60.dp)
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
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }

        homeInfoData.value?.data?.let { characterData ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = 0.dp, y = 200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 닉네임
                StrokedText(
                    text = characterData.nickname,
                    fontSize = 40,
                    strokeColor = Color(0xFF701F3D),
                    strokeWidth = 25f
                )

                // 캐릭터 이미지
                val painter = rememberAsyncImagePainter(model = characterData.characterImage)
                Image(
                    painter = painter,
                    contentDescription = "Cat Character",
                    modifier = Modifier
                        .size(230.dp)
                        .offset(x = 20.dp)
                )

//                AsyncImage(
//                    model = characterData.characterImage,
//                    contentDescription = "Cat Character",
//                    modifier = Modifier
//                        .size(230.dp)
//                        .offset(x = 20.dp)
//                        .fillMaxWidth()
//                )

                Spacer(modifier = Modifier.height(16.dp))

                // 정보
                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .height(185.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xD70D1314)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // 레벨 표시
                            Text(
                                text = "Lv.${characterData.level}",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFDA0A)
                            )
                            Spacer(modifier = Modifier.width(14.dp))

                            // 레벨 게이지
                            val progress =
                                (characterData.experience.toFloat() / 1000f).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .width(170.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFC4C4C4))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress) // 현재 ~~% 진행
                                        .height(14.dp)
                                        .background(Color(0xFFFFDA0A))
                                )
                                Text(
                                    text = "${characterData.experience}/1000",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF414141),
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))

                        Row() {
                            // 코인 표시
                            ReusableInfoBox(
                                value = characterData.coin.toString(),
                                label = "캔코인",
                                iconResId = R.drawable.home_img_cancoin
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // 게임 횟수 표시
                            ReusableInfoBox(
                                value = "${characterData.wins}승 ${characterData.losses}패",
                                label = "${characterData.totalGames}판",
                                iconResId = R.drawable.home_img_game,
                                mainFontSize = 30,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

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
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.home_btn_start),
                            contentDescription = "Start Button",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        StrokedText(
                            text = "START",
                            color = Color(0xFFFFFFFF),
                            fontSize = 42,
                            strokeColor = Color(0xff36DBEB),
                            strokeWidth = 20f,
                            letterSpacing = 7f
                        )
                    }
                }
            }
        }

        // 랭킹 모달 표시
        if (showRanking) {
            RankingDialog(onDismiss = { showRanking = false }, rankingData = rankingData)
        }

        // 프로필 모달 표시
        if (showProfile) {
            ProfileDialog(onDismiss = { showProfile = false }, profileData = profileData)
        }

    }
}


fun Modifier.innerShadow(
    shape: Shape,
    color: Color = Color.Black,
    blur: Dp = 4.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 2.dp,
    spread: Dp = 0.dp
) = this.drawWithContent {

    drawContent()

    drawIntoCanvas { canvas ->

        val shadowSize = Size(size.width + spread.toPx(), size.height + spread.toPx())
        val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

        val paint = Paint()
        paint.color = color

        canvas.saveLayer(size.toRect(), paint)
        canvas.drawOutline(shadowOutline, paint)

        paint.asFrameworkPaint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            if (blur.toPx() > 0) {
                maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
        }

        paint.color = Color.Black

        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)
        canvas.restore()
    }
}


@Composable
fun ReusableInfoBox(
    value: String,
    label: String,
    iconResId: Int,
    mainFontSize: Int = 40,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x9E2A4042))
            // Glare effect
            .innerShadow(
                shape = RectangleShape, color = Color.White.copy(0.56f),
                offsetY = (-2).dp, offsetX = (-2).dp
            )
            // Shadow effect
            .innerShadow(
                shape = RectangleShape, color = Color.Black.copy(0.56f),
                offsetY = 2.dp, offsetX = 2.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Value Text
            Text(
                text = value,
                fontSize = mainFontSize.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            Row(
                modifier = if (label != "캔코인") {
                    modifier.offset(y = 4.dp)
                } else modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.width(4.dp))

                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = label,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}
