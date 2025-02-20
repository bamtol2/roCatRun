package com.eeos.rocatrun.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.LocalTextStyle
import com.eeos.rocatrun.R

@Composable
fun FeverTime() {
    val infiniteTransition = rememberInfiniteTransition()

    // 깜빡이는 효과를 위한 알파값 애니메이션
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = alpha),
        contentAlignment = Alignment.Center
    ){
//     {
//        // 공격 버튼
//        Box(
//            modifier = Modifier
//                .size(80.dp)
//                .align(Alignment.Center),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "공격",
//                color = Color(0xFF00FFCC),
//                fontSize = 20.sp,
//                fontFamily = FontFamily(Font(R.font.neodgm))
//            )
//        }

        // 피버타임 텍스트 배치
        Box(
            modifier = Modifier.fillMaxSize().padding(50.dp)
        ) {
            Row {

                Column {
                    FeverTimeText(
                        text = "피",
                        modifier = Modifier.offset(x= (-8).dp, y = 30.dp)

                    )

                    FeverTimeText(
                        text = "버",
                        modifier = Modifier.offset(y = 30.dp, x = (-8).dp)

                    )
                }

                Column {
                    FeverTimeText(
                        text = "타",
                        modifier = Modifier.offset(y=31.dp, x = 38.dp)
                    )

                    FeverTimeText(
                        text = "임",
                        modifier = Modifier.offset(y=30.dp, x= 38.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FeverTimeText(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(50.dp)) {
        // 스트로크 텍스트
        Text(
            text = text,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFED2F2F),
            fontFamily = FontFamily(Font(R.font.neodgm)),
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(
                drawStyle = Stroke(
                    width = 8f,
                    join = StrokeJoin.Round
                )
            )
        )

        // 내부 텍스트
        Text(
            text = text,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFFF00),
            fontFamily = FontFamily(Font(R.font.neodgm)),
            textAlign = TextAlign.Center
        )
    }
}