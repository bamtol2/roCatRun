package com.eeos.rocatrun.stats

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.eeos.rocatrun.R
import androidx.compose.ui.window.Dialog
import com.eeos.rocatrun.ui.theme.MyFontFamily
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import java.util.*

@Composable
fun MonStatsScreen() {
    var isDialogVisible by remember { mutableStateOf(false) } // 다이얼로그의 표시 여부 상태
    var selectedDate by remember { mutableStateOf("2023년 5월") } // 선택된 날짜를 저장하는 상태

    val (year, month) = selectedDate.split("년", "월").let {
        it[0].toInt() to it[1].trim().toInt()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.stats_bg_week_mon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 주차 선택
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                StrokedText(
                    text = selectedDate,
                    color = Color.White,
                    strokeColor = Color.Black,
                    fontSize = 25,
                )
                Image(
                    painter = painterResource(id = R.drawable.stats_icon_dropdown),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            isDialogVisible = true
                        }
                        .offset(x = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // DatePickerDialog 보여주기
            if (isDialogVisible) {
                DatePickerDialog2(
                    initialYear = year,
                    initialMonth = month,
                    onDateSelected = { selectedYear, selectedMonth ->
                        selectedDate = "${selectedYear}년 ${selectedMonth}월" // 날짜 선택 후 상태 업데이트
                        isDialogVisible = false
                    },
                    onDismiss = {
                        isDialogVisible = false
                    }
                )
            }

            // 총 거리
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .width(250.dp)
                    .height(80.dp)
                    .border(
                        width = 3.dp,
                        color = Color(0xFF36DBEB),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                StrokedText(
                    text = "50.6 KM",
                    color = Color.White,
                    strokeColor = Color(0xFF34B4C0),
                    fontSize = 50,
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 러닝 정보
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "러닝", value = "3")
                StatItem(label = "페이스", value = "05'43\"")
                StatItem(label = "총 시간", value = "01h 48m")
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 그래프
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0x8200001E), RoundedCornerShape(8.dp))
            ) {
//                BarGraph2()
                BarGraph3(month = month, year = year)
            }
        }
    }
}

@Composable
fun BarGraph3(month: Int, year: Int) {
    // 해당 달의 일 수 계산
    val monthDays = getDaysInMonth(year, month)

    // 더미 데이터 생성
    val dataList = (1..monthDays).map { day ->
        val value = if (day % 5 == 0) 0.0 else (10..100).random().toDouble()
        day to value
    }

    // Bars 데이터 생성
    val chartData = dataList.map { (day, value) ->
        Bars(
            label = "$day",
            values = listOf(
                Bars.Data(
                    value = value,
                    color = Brush.verticalGradient(
                        colors = listOf(Color.Blue, Color.Green),
                        startY = 0f,
                        endY = 1000f
                    )
                )
            )
        )
    }

    // 라벨 생성
    val labels = getLabels(month, year, monthDays)

    ColumnChart(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp, bottom = 22.dp, start = 22.dp, end = 22.dp),
        data = remember { chartData },
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 3.dp,
            thickness = 5.dp
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            textStyle = TextStyle(
                fontFamily = MyFontFamily,
                color = Color.White
            ),
            count = IndicatorCount.CountBased(count = 5),
            position = IndicatorPosition.Horizontal.Start,
            padding = 16.dp,
            contentBuilder = { indicator ->
                "%.2f".format(indicator)
            },
            indicators = listOf(100.0, 80.0, 60.0, 40.0, 20.0)
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle(
                fontFamily = MyFontFamily,
                color = Color.White
            ),
            labels = labels
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = false,
        ),
        dividerProperties = DividerProperties(
            enabled = true,
            xAxisProperties = LineProperties(
                enabled = true,
                style = StrokeStyle.Normal,
                color = Brush.horizontalGradient(colors = listOf(Color.Blue, Color.White)),
                thickness = (.5).dp,
            ),
            yAxisProperties = LineProperties(
                enabled = true,
                style = StrokeStyle.Normal,
                color = Brush.verticalGradient(colors = listOf(Color.White, Color.Blue)),
                thickness = (.5).dp,
            )
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = GridProperties.AxisProperties(
                enabled = true,
                style = StrokeStyle.Dashed(intervals = floatArrayOf(10f, 10f)),
                color = SolidColor(Color.White),
                thickness = (.5).dp,
                lineCount = 5
            ),
            yAxisProperties = GridProperties.AxisProperties(
                enabled = false,
            )
        )
    )
}

// 해당 달의 일 수 계산하는 함수
fun getDaysInMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

// 라벨 생성하는 함수 : 첫날, 마지막 날, 월요일만 표시
fun getLabels(month: Int, year: Int, monthDays: Int): List<String> {
    val labels = mutableListOf<String>()

    labels.add("1")

    val calendar = Calendar.getInstance()
    for (day in 2 until monthDays) {
        calendar.set(year, month - 1, day)
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            labels.add("$day")
        }
    }

    if (monthDays > 1) labels.add("$monthDays")

    return labels
}


// 날짜 선택 모달
@Composable
fun DatePickerDialog2(
    initialYear: Int,
    initialMonth: Int,
    onDateSelected: (year: Int, month: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val years = (1900..Calendar.getInstance().get(Calendar.YEAR)).toList()
    val months = (1..12).toList()

    var selectedYear by remember { mutableStateOf(initialYear) } // initialYear로 초기화
    var selectedMonth by remember { mutableStateOf(initialMonth) } // initialMonth로 초기화

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "주차 선택",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WheelPicker(items = years, selectedItem = selectedYear) { selectedYear = it }
                    WheelPicker(items = months, selectedItem = selectedMonth) { selectedMonth = it }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            fontFamily = MyFontFamily
                        )
                    }
                    TextButton(onClick = {
                        onDateSelected(
                            selectedYear,
                            selectedMonth
                        )
                    }) {
                        Text(
                            text = "OK",
                            fontFamily = MyFontFamily
                        )
                    }
                }
            }
        }
    }
}
