package com.eeos.rocatrun.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eeos.rocatrun.R
import com.eeos.rocatrun.ui.theme.MyFontFamily

data class DailyData(
    val date: String,       // 날짜 (예: "2025-01-01")
    val pace: String,       // 페이스 (예: "05'43\"")
    val calories: Double,   // 칼로리 (예: 612)
    val cadence: Int,       // 케이던스 (분당 걸음 수, 예: 160)
    val distance: Double,   // 거리 (km, 예: 5.5)
    val time: String        // 시간 (예: "01h 30m 20s")
)

@Composable
fun DetailDialog(onDismiss: () -> Unit) {
    val dailyStats = DailyData(
        date = "2025/01/22",
        pace = "05'43\"",
        calories = 200.5,
        cadence = 160,
        distance = 5.5,
        time = "01h 30m 20s"
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(650.dp)
                .padding(0.dp),
            contentAlignment = Alignment.Center,
        ) {
            // 모달 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.stats_bg_detail_image),
                contentDescription = "Modal Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 15.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 모달 Title (날짜)
                Text(
                    text = dailyStats.date,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(30.dp))

                // 개인 기록 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatColumn(label = "페이스", value = "05'48\"")
                    StatColumn(label = "칼로리", value = "612kcal")
                    StatColumn(label = "케이던스", value = "162spm")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatColumn(label = "거리", value = "11.5km")
                    StatColumn(label = "시간", value = "01:12:35")
                }
                Spacer(modifier = Modifier.height(30.dp))

                Image(
                    painter = painterResource(id = R.drawable.stats_img_map),
                    contentDescription = "Route Map",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))

                // 확인 버튼
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = Color(0xFF36DBEB),
                            shape = RoundedCornerShape(15.dp)
                        ),
                ) {
                    Text(
                        text = "확인",
                        style = TextStyle(
                            fontFamily = MyFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 20.sp,
            color = Color.White,
            fontFamily = MyFontFamily
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(50.dp)
                .background(Color.Transparent)
                .border(2.dp, Color(0xFF36DBEB), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            StrokedText(
                text = value,
                color = Color.White,
                strokeColor = Color(0xFF34B4C0),
                fontSize = 20,
            )
        }
    }
}

