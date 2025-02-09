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
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.viewinterop.AndroidView
import com.eeos.rocatrun.R
import com.eeos.rocatrun.component.CircularItemGauge
import com.eeos.rocatrun.component.FeverTime
//import android.content.Context
//import android.os.VibrationEffect
//import android.os.Vibrator
import androidx.activity.viewModels
import com.eeos.rocatrun.viewmodel.GameViewModel


class ItemActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoCatRunTheme {
                GameScreen(gameViewModel)
            }
        }
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

@Composable
fun GameScreen(gameViewModel: GameViewModel) {
    val context = LocalContext.current
    val itemGaugeValue by gameViewModel.itemGaugeValue.collectAsState()
    val bossGaugeValue by gameViewModel.bossGaugeValue.collectAsState()
    val feverTimeActive by gameViewModel.feverTimeActive.collectAsState()
    val showItemGif by gameViewModel.showItemGif.collectAsState()
    val maxGaugeValue = 100

    var itemUsageCount by remember { mutableIntStateOf(0) } // 아이템 사용 횟수 추적



    val itemProgress by animateFloatAsState(
        targetValue = itemGaugeValue.toFloat() / maxGaugeValue,
        animationSpec = tween(durationMillis = 500)
    )
    val bossProgress by animateFloatAsState(
        targetValue = bossGaugeValue.toFloat() / maxGaugeValue,
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
                modifier = Modifier.size(200.dp)
            )


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                // 고양이 GIF
                AnimatedGifView(
                    resourceId = R.drawable.wear_gif_rainbowcat,
                    modifier = Modifier.size(130.dp)
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
                modifier = Modifier.size(200.dp)
            )


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                // 고양이 GIF
                AnimatedGifView(
                    resourceId = R.drawable.wear_gif_movewhitecat,
                    modifier = Modifier.size(80.dp)
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


    }


    // "게이지 올리기" 버튼 (상단에 작게 배치)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Button(
            onClick = {
                gameViewModel.increaseItemGauge()
                if (gameViewModel.itemGaugeValue.value == 100) {
                    gameViewModel.handleGaugeFull(context)
                }
            },
            modifier = Modifier.wrapContentSize() // 버튼 크기 조정
        ) {
            Text("게이지+", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.neodgm)))
        }
    }
}