package com.eeos.rocatrun.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun CircularItemGauge(
    itemProgress: Float,
    bossProgress: Float,
    modifier: Modifier = Modifier,
    isFeverTime : Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val bossStrokeWidth = 6.dp.toPx()
            val itemStrokeWidth = 6.dp.toPx()
            val gapBetweenGauges = 8.dp.toPx()

            // 보스 게이지
            drawArc(
                color = Color(0xFFFF00CC),
                startAngle = -90f,
                sweepAngle = 360f * bossProgress,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = bossStrokeWidth,
                    cap = StrokeCap.Round
                )
            )

            val itemGaugeColor = if (isFeverTime) Color(0XFFF3F123) else Color.Cyan

            // 아이템 게이지
            val itemInset = (bossStrokeWidth / 2) + (gapBetweenGauges / 2)
            drawArc(
                color = itemGaugeColor,
                startAngle = -90f,
                sweepAngle = 360f * itemProgress,
                useCenter = false,
                topLeft = Offset(itemInset, itemInset),
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
}
