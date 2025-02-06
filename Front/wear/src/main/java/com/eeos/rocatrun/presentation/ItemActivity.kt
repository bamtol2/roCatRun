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
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.viewinterop.AndroidView
import com.eeos.rocatrun.R
import kotlinx.coroutines.delay
import com.eeos.rocatrun.component.CircularItemGauge
import com.eeos.rocatrun.component.FeverTime
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.content.ContextCompat.getSystemService
import com.eeos.rocatrun.viewmodel.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

fun triggerVibration(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        val vibrationEffect = VibrationEffect.createOneShot(
            500,  // 진동 시간 (밀리초)
            VibrationEffect.DEFAULT_AMPLITUDE
        )
        vibrator.vibrate(vibrationEffect)
    }
}
@Composable
fun GameScreen() {
    var context = LocalContext.current
    var coroutineScope = rememberCoroutineScope()
    var itemGaugeValue by remember { mutableIntStateOf(0) }
    var bossGaugeValue by remember { mutableIntStateOf(100) }
    var showItemGif by remember { mutableStateOf(false) }
    val maxGaugeValue = 100
    var feverTimeActive by remember { mutableStateOf(false) } // 피버타임 플래그
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
        } else {
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
                AnimatedGifView(resourceId = R.drawable.wear_gif_movewhitecat)

                // 아이템 GIF (조건부 표시)
                if (showItemGif) {
                    AnimatedGifView(resourceId = R.drawable.wear_gif_spincan)
                }
            }
        }
    }

    // 아이템 게이지가 다 찼을 때 자동 공격 및 GIF 표시
    LaunchedEffect(itemGaugeValue) {
        if (itemGaugeValue == maxGaugeValue) {
            itemUsageCount++
            Log.i("아이템 횟수", "아이템 횟수 : $itemUsageCount")
            showItemGif = true
            delay(1000)  // 1초 동안 GIF 유지

            bossGaugeValue = (bossGaugeValue - 400).coerceAtLeast(0)
            showItemGif = false


            // 아이템 2번 사용 시 피버 타임 활성화
            if(itemUsageCount == 2){
                feverTimeActive = true
                coroutineScope.launch {
                    triggerVibration(context)
                }
                delay(30000) // 30초간 피버타임
                feverTimeActive = false
                itemUsageCount = 0
            }
        }
    }

//    // "게이지 올리기" 버튼 (상단에 작게 배치)
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top,
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(top = 16.dp)
//    ) {
//        Button(
//            onClick = {
//                itemGaugeValue = (itemGaugeValue + 20).coerceAtMost(maxGaugeValue)
//            },
//            modifier = Modifier.wrapContentSize() // 버튼 크기 조정
//        ) {
//            Text("게이지+", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.neodgm)))
//        }
//    }
}