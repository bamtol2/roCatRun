package com.eeos.rocatrun.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke


// 스트로크 글씨 함수
@Composable
fun StrokedText(
    text: String,
    fontSize: Int,
    strokeWidth: Float = 10f,
    color: Color = Color.White,
    strokeColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Box {
        // Stroke 텍스트
        Text(
            text = text,
            color = strokeColor,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = fontSize.sp,
                drawStyle = Stroke(
                    width = strokeWidth,
                    join = StrokeJoin.Round
                )
            ),
            modifier = modifier
        )
        // 일반 텍스트
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                color = color,
                fontSize = fontSize.sp
            ),
            modifier = modifier
        )
    }
}