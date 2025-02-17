package com.eeos.rocatrun.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.eeos.rocatrun.R
import com.eeos.rocatrun.closet.CharacterWithItems
import com.eeos.rocatrun.closet.ClosetActivity
import com.eeos.rocatrun.closet.api.ClosetViewModel
import com.eeos.rocatrun.game.AlertScreen
import com.eeos.rocatrun.game.GameRoom
import com.eeos.rocatrun.home.api.HomeViewModel
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.ppobgi.PpobgiDialog
import com.eeos.rocatrun.profile.ProfileDialog
import com.eeos.rocatrun.profile.api.ProfileViewModel
import com.eeos.rocatrun.ranking.RankingDialog
import com.eeos.rocatrun.ranking.api.RankingViewModel
import com.eeos.rocatrun.shop.ShopActivity
import com.eeos.rocatrun.stats.StatsActivity
import com.eeos.rocatrun.ui.components.StrokedText
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random


@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    val context = LocalContext.current
    val token = TokenStorage.getAccessToken(context)

    // 알림창 상태 관리
    var showAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

    // 블루투스/워치 연결 확인 함수
    fun checkBluetoothAndWear() {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        when {
            bluetoothAdapter == null -> {
                alertMessage = "이 기기는 블루투스를 지원하지 않는다냥!"
                showAlert = true
            }
            !bluetoothAdapter.isEnabled -> {
                alertMessage = "블루투스를 켜달라냥!"
                showAlert = true
            }
            else -> {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val nodes = Wearable.getNodeClient(context).connectedNodes.await()
                        if (nodes.isNotEmpty()) {
                            context.startActivity(Intent(context, GameRoom::class.java))
                        } else {
                            alertMessage = "워치를 연결 하라냥!"
                            showAlert = true
                        }
                    } catch (e: Exception) {
                        alertMessage = "워치 연결 상태를 확인할 수 없다냥!"
                        showAlert = true
                    }
                }
            }
        }
    }

    // ViewModel에서 가져온 데이터
    val homeInfoData = homeViewModel.homeData.observeAsState()

    // 인벤토리 목록
    val closetViewModel : ClosetViewModel = viewModel()
    val inventoryList = closetViewModel.inventoryList.value

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

    // 뽑기 모달 변수
    var showPpobgi by remember { mutableStateOf(false) }

    // 랜덤 텍스트
    var showText by remember { mutableStateOf(false) }
    var randomX by remember { mutableStateOf(0f) }
    var randomY by remember { mutableStateOf(0f) }
    var randomText by remember { mutableStateOf("") }
    val animatedX by animateFloatAsState(targetValue = randomX, animationSpec = tween(100))
    val animatedY by animateFloatAsState(targetValue = randomY, animationSpec = tween(100))
    val textList = listOf(
        "왜 건드리냥?",
        "간식 줄거냥?",
        "달려서\n화성 갈끄니까~",
        "놀아주고 싶냥?",
        "쓰다듬지 마냥!",
        "피곤하다냥~",
        "잠온다냥...",
        "배고프다냥!",
        "언제 달리냥??",
        "흥!",
        "왜 건드리냥?",
        "간식 줄거냥?",
        "놀아주고 싶냥?",
        "쓰다듬지 마냥!",
        "피곤하다냥~",
        "잠온다냥...",
        "배고프다냥!",
        "언제 달리냥??",
        "규리다냥",
        "왜 건드리냥?",
        "간식 줄거냥?",
        "놀아주고 싶냥?",
        "쓰다듬지 마냥!",
        "피곤하다냥~",
        "잠온다냥...",
        "배고프다냥!",
        "언제 달리냥??",
        "왜 건드리냥?",
        "간식 줄거냥?",
        "놀아주고 싶냥?",
        "쓰다듬지 마냥!",
        "피곤하다냥~",
        "잠온다냥...",
        "먀오~",
        "배고프다냥!",
        "언제 달리냥??",
        "왜 건드리냥?",
        "간식 줄거냥?",
        "놀아주고 싶냥?",
        "쓰다듬지 마냥!",
        "피곤하다냥~",
        "이 편지는 싸피에서\n최초로 시작되어\n일년에 한 바퀴\n돌면서 받는\n사람에게 행운을\n주었고...",
        "잠온다냥...",
        "배고프다냥!",
        "언제 달리냥??",
        "흥!",
    )

    // 랜덤 텍스트 생성 및 위치 업데이트
    LaunchedEffect(key1=showText) {
        if (showText) {
            randomText = textList[Random.nextInt(textList.size)]
            randomX = Random.nextFloat() * 200f - 30f
            randomY = Random.nextFloat() * 200f - 30f
            delay(1000)
            showText = false
            randomText=""
        }
    }

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
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                // 왼쪽 상단 버튼들 (랭킹, 뽑기)
                Button(
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
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showPpobgi = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.home_icon_ppobgi),
                        contentDescription = "Ppobgi Icon",
                        modifier = Modifier.size(60.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { context.startActivity(Intent(context, ShopActivity::class.java)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.home_icon_shop),
                        contentDescription = "Shop Icon",
                        modifier = Modifier.size(60.dp)
                    )
                }
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
                    onClick = {
                        context.startActivity(
                            Intent(
                                context,
                                ClosetActivity::class.java
                            )
                        )
                    },
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
                    fontSize = 35,
                    strokeColor = Color(0xFF701F3D),
                    strokeWidth = 25f
                )

                // 캐릭터 이미지
                Box(
                    modifier = Modifier.clickable { showText = true }
                ) {
                    if (characterData.characterImage == "default.png") {
                        Image(
                            painter = rememberAsyncImagePainter("android.resource://com.eeos.rocatrun/${R.drawable.all_img_whitecat}"),
                            contentDescription = "Cat Character",
                            modifier = Modifier
                                .size(230.dp)
                                .offset(x = 20.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        CharacterWithItems(wornItems = inventoryList.filter { it.equipped })
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 랜덤 텍스트
                    if (showText) {
                        Box(
                            modifier = Modifier
                                .offset(animatedX.dp, animatedY.dp)
                                .background(Color.Black, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .zIndex(1f)
                        ) {
                            Text(
                                text = randomText,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))

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
                                (characterData.experience.toFloat() / characterData.requiredExpForNextLevel.toFloat()).coerceIn(0f, 1f)
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
                                    text = "${characterData.experience}/${characterData.requiredExpForNextLevel}",
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

                Spacer(modifier = Modifier.height(20.dp))

                // START 버튼
                Button(
                    onClick = {
                        checkBluetoothAndWear()
                    },
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

                // 알림창 표시
                if (showAlert) {
                    AlertScreen(
                        message = alertMessage,
                        onDismissRequest = { showAlert = false }
                    )
                }
            }
        }

        VersionInfo()

        // 랭킹 모달 표시
        if (showRanking) {
            RankingDialog(onDismiss = { showRanking = false }, rankingData = rankingData)
        }

        // 프로필 모달 표시
        if (showProfile) {
            ProfileDialog(onDismiss = { showProfile = false }, profileData = profileData)
        }

        // 뽑기 모달 표시
        if (showPpobgi) {
            PpobgiDialog(
                onDismiss = { showPpobgi = false },
                refreshHomeData = {
                    homeViewModel.fetchHomeInfo(token)
                }
            )
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

//버전 정보 표시
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VersionInfo() {
    val context = LocalContext.current
    val versionText = context.getString(R.string.app_version)

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = versionText,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 15.dp)
        )
    }
}