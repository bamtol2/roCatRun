package com.eeos.rocatrun.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.presentation.theme.RoCatRunTheme
import android.graphics.drawable.AnimatedImageDrawable
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.viewinterop.AndroidView
import com.eeos.rocatrun.R
import com.eeos.rocatrun.ui.CircularItemGauge
import com.eeos.rocatrun.ui.FeverTime
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.eeos.rocatrun.viewmodel.BossHealthRepository
import com.eeos.rocatrun.viewmodel.GameViewModel
import com.eeos.rocatrun.viewmodel.MultiUserViewModel



class ItemActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()
    private val multiUserViewModel: MultiUserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoCatRunTheme {
                GameScreen(gameViewModel, multiUserViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finishAndRemoveTask()
    }
}


@Composable
fun AnimatedGifView(resourceId: Int, modifier: Modifier) {
    val context = LocalContext.current
    val drawable = remember {
        context.getDrawable(resourceId) as? AnimatedImageDrawable
    }

    // 애니메이션 시작
    LaunchedEffect(drawable) {
        drawable?.start()
    }

    // AndroidView를 통해 AnimatedImageDrawable 표시
    AndroidView(
        factory = { ImageView(it).apply { setImageDrawable(drawable) } },
        modifier = modifier
    )
}

fun formatTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

@Composable
fun GameScreen(gameViewModel: GameViewModel, multiUserViewModel: MultiUserViewModel) {
    val context = LocalContext.current
    val remainingTime by BossHealthRepository.gameTime.collectAsState()

    var fomattedTime = formatTime(remainingTime)
    val itemGaugeValue by gameViewModel.itemGaugeValue.collectAsState()
    val bossGaugeValue by gameViewModel.bossGaugeValue.collectAsState()
    val feverTimeActive by gameViewModel.feverTimeActive.collectAsState()
    val showItemGif by gameViewModel.showItemGif.collectAsState()
    val itemCount by gameViewModel.availableItemCount.collectAsState()
    val isFeverTime by gameViewModel.feverTimeActive.collectAsState()
    val maxGaugeValue = 100

    // BossHealthRepository의 최대 체력 구독 (최초 값이 0이라면 기본값 10000 사용)
    val maxBossHealth by BossHealthRepository.maxBossHealth.collectAsState()
    val effectiveMaxBossHealth = if (maxBossHealth == 0) 10000 else maxBossHealth

    // 제한시간 색깔 지정
    val timeColor = when {
        remainingTime <= 10 -> Color.Red  // 10초 이하일 때 무조건 빨간색
        feverTimeActive -> Color.Yellow
        else -> Color(0xFF00E2B1)
    }

    // 피버 이벤트 관찰 시작
    LaunchedEffect(Unit) {
        gameViewModel.observeFeverEvents(multiUserViewModel, context)
    }

    val itemProgress by animateFloatAsState(
        targetValue = itemGaugeValue.toFloat() / maxGaugeValue,
        animationSpec = tween(durationMillis = 500)
    )
    val bossProgress by animateFloatAsState(
        targetValue = bossGaugeValue.toFloat() / effectiveMaxBossHealth,
        animationSpec = tween(durationMillis = 500)
    )


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        if (feverTimeActive) {
            FeverTime()

            // 원형 게이지 표시
            CircularItemGauge(
                itemProgress = itemProgress,
                bossProgress = bossProgress,
                modifier = Modifier.size(200.dp),
                isFeverTime
            )
            Text(
                text = "$fomattedTime",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.neodgm)),
                color = timeColor,
                modifier = Modifier.offset(y = -40.dp)
            )


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                // 고양이 GIF
                AnimatedGifView(
                    resourceId = R.drawable.wear_gif_movewhitecat,
                    modifier = Modifier.size(60.dp)
                )

                // 아이템 GIF (조건부 표시)
                if (showItemGif) {
                    AnimatedGifView(
                        resourceId = R.drawable.wear_gif_spincan,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

        }else{
            // 원형 게이지 표시
            CircularItemGauge(
                itemProgress = itemProgress,
                bossProgress = bossProgress,
                modifier = Modifier.size(200.dp),
                isFeverTime
            )
            Text(
                text = "$fomattedTime",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.neodgm)),
                color = timeColor,
                modifier = Modifier.offset(y = -40.dp)
            )


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                // 고양이 GIF
                AnimatedGifView(
                    resourceId = R.drawable.wear_gif_movewhitecat,
                    modifier = Modifier.size(60.dp)
                    )

                // 아이템 GIF (조건부 표시)
                if (showItemGif) {
                    AnimatedGifView(
                        resourceId = R.drawable.wear_gif_spincan,
                        modifier = Modifier.size(60.dp)
                        )
                }
            }
        }
        if (itemCount > 0) {
            Button(
                onClick = {
                    if (!gameViewModel.itemUsedSignal.value) {  // 중복 실행 방지
                        gameViewModel.notifyItemUsage(context)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FFCC)
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .width(60.dp)
                    .height(30.dp)
                    .padding(horizontal = 2.dp)
                    .offset(y = 60.dp)
            ) {
                Text(
                    text = "공격",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.neodgm))
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false  // 줄바꿈 방지
                )
            }
        }
    }


    // "게이지 올리기" 버튼 (상단에 작게 배치)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.wear_icon_fish),
                contentDescription = "아이템",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = " X ${itemCount}",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.neodgm)),
                color = Color.White
            )


//        Button(
//            onClick = {
//                gameViewModel.setItemGauge(50)
//                if (gameViewModel.itemGaugeValue.value == 100) {
//                    gameViewModel.handleGaugeFull(context)
//                }
//            },
//            modifier = Modifier
//                .width(30.dp)
//                .height(30.dp)
//        ) {
//            Text("+", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.neodgm)))
//        }
        }
    }
}