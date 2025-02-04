package com.eeos.rocatrun.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.delay

class ItemActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoCatRunTheme {
                GameScreen()
            }
        }
    }
}

@Composable
fun CircularItemGaugeWithBoss(
    itemProgress: Float,      // 아이템 게이지 (0.0f ~ 1.0f)
    bossProgress: Float,      // 보스 게이지 (0.0f ~ 1.0f)
    modifier: Modifier = Modifier.size(200.dp)
) {
    Canvas(modifier = modifier) {
        val bossStrokeWidth = 4.dp.toPx()    // 보스 게이지 두께
        val itemStrokeWidth = 4.dp.toPx()     // 아이템 게이지 두께
        val gapBetweenGauges = 4.dp.toPx()     // 두 게이지 간의 간격을 좁게 설정

        // 1. 보스 게이지 (바깥쪽 원형)
        drawArc(
            color = Color.Red,
            startAngle = -90f,
            sweepAngle = 360f * bossProgress,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = bossStrokeWidth,
                cap = StrokeCap.Round
            )
        )

        // 2. 아이템 게이지 (보스 게이지 바로 옆에 표시)
        val itemInset = (bossStrokeWidth / 2) + (gapBetweenGauges / 2)
        drawArc(
            color = Color.Cyan,
            startAngle = -90f,
            sweepAngle = 360f * itemProgress,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(itemInset, itemInset),
            size = size.copy(
                width = size.width - 2 * itemInset,
                height = size.height - 2 * itemInset
            ),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = itemStrokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}
@Composable
fun AnimatedGifView(resourceId: Int) {
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
        modifier = Modifier.size(80.dp)
    )
}

@Composable
fun GameScreen() {
    var itemGaugeValue by remember { mutableIntStateOf(0) }
    var bossGaugeValue by remember { mutableIntStateOf(100) }
    var showItemGif by remember { mutableStateOf(false) }
    val maxGaugeValue = 100

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
        // 원형 게이지 표시
        CircularItemGaugeWithBoss(itemProgress = itemProgress, bossProgress = bossProgress)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // 고양이 GIF
            AnimatedGifView(resourceId = R.drawable.wear_gif_movewhitecat)

            // 아이템 GIF (조건부 표시)
            if (showItemGif) {
                AnimatedGifView(resourceId = R.drawable.wear_gif_spincan)
            }
        }
    }

    // 아이템 게이지가 다 찼을 때 자동 공격 및 GIF 표시
    LaunchedEffect(itemGaugeValue) {
        if (itemGaugeValue == maxGaugeValue) {
            showItemGif = true
            delay(1000)  // 1초 동안 GIF 유지
            itemGaugeValue = 0
            bossGaugeValue = (bossGaugeValue - 20).coerceAtLeast(0)
            showItemGif = false
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
                itemGaugeValue = (itemGaugeValue + 20).coerceAtMost(maxGaugeValue)
            },
            modifier = Modifier.wrapContentSize() // 버튼 크기 조정
        ) {
            Text("게이지+", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.neodgm)))
        }
    }
}