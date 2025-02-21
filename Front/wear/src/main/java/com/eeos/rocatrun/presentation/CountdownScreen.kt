package com.eeos.rocatrun.presentation

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import kotlinx.coroutines.delay

@Composable
fun CountdownScreen(
    onFinish: () -> Unit  // 카운트다운 종료 시 실행할 함수
) {
    val context = LocalContext.current
    var countdownValue by remember { mutableIntStateOf(5) }

    LaunchedEffect(Unit) {
        val vibrator = getVibrator(context)
        while (countdownValue > 0) {
            vibrate(vibrator) // 진동 발생
            delay(1000) // 1초 대기
            countdownValue -= 1
        }
        onFinish() // 카운트다운 종료 시 실행
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = countdownValue.toString(),
            color = Color.White,
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.neodgm))
        )
    }
}

// 진동 발생 함수
private fun vibrate(vibrator: Vibrator) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(100)
    }
}

// Vibrator 가져오는 함수 (버전 대응)
private fun getVibrator(context: Context): Vibrator {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val vibManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibManager.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}
