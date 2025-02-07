package com.eeos.rocatrun.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.eeos.rocatrun.R

@Composable
fun FeverTime() {
    val infiniteTransition = rememberInfiniteTransition()

    // 개별 텍스트에 대한 애니메이션 설정
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFD700),
        targetValue = Color(0xFFFF4500),
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // 피버타임 텍스트 레이아웃
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val fontFamily = FontFamily(Font(R.font.neodgm))

        // 각 글자에 애니메이션 적용
        FeverTimeAnimatedLetter("피", color, scale, alpha, fontFamily)
        Spacer(modifier = Modifier.height(4.dp))
        FeverTimeAnimatedLetter("버", color, scale, alpha, fontFamily)
        Spacer(modifier = Modifier.height(4.dp))
        FeverTimeAnimatedLetter("타", color, scale, alpha, fontFamily)
        Spacer(modifier = Modifier.height(4.dp))
        FeverTimeAnimatedLetter("임", color, scale, alpha, fontFamily)
    }
}

@Composable
fun FeverTimeAnimatedLetter(
    letter: String,
    color: Color,
    scale: Float,
    alpha: Float,
    fontFamily: FontFamily
) {
    Text(
        text = letter,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        fontFamily = fontFamily,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            )
    )
}
