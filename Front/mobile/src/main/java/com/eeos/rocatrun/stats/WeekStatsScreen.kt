package com.eeos.rocatrun.stats

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
fun WeekStatsScreen() {
    var isDialogVisible by remember { mutableStateOf(false) } // 다이얼로그의 표시 여부 상태
    var selectedDate by remember { mutableStateOf("2025년 1월 4주") } // 선택된 날짜를 저장하는 상태

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
                Text(
                    text = selectedDate,
                    fontSize = 25.sp,
                    color = Color.White
                )
                Icon(
                    painter = painterResource(id = R.drawable.stats_icon_dropdown),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            isDialogVisible = true
                        }
                        .offset(x = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // DatePickerDialog 보여주기
            if (isDialogVisible) {
                DatePickerDialog(
                    onDateSelected = { week, month, year ->
                        selectedDate = "${year}년 ${month}월 ${week}주" // 날짜 선택 후 상태 업데이트
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
                Text(
                    text = "20.6 KM",
                    fontSize = 50.sp,
                    color = Color(0xFFFFFFFF),
                    fontWeight = FontWeight.Bold,
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
                BarGraph()
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 35.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

@Composable
fun BarGraph() {
    // 더미 데이터
    val dataList = listOf(
        "월" to 20.0,
        "화" to 50.0,
        "수" to 80.0,
        "목" to 30.0,
        "금" to 60.0,
        "토" to 40.0,
        "일" to 70.0
    )

    // Bars 데이터 생성
    val chartData = dataList.map { (day, value) ->
        Bars(
            label = day,
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

    ColumnChart(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp, bottom = 22.dp, start = 22.dp, end = 22.dp),
        data = remember { chartData },
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 3.dp,
            thickness = 20.dp
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
            indicators = listOf(80.0, 60.0, 40.0, 20.0, 0.0)
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle(
                fontFamily = MyFontFamily,
                color = Color.White
            ),
            labels = listOf("월", "화", "수", "목", "금", "토", "일")
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
        gridProperties  = GridProperties(
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


// 날짜 선택 모달
@Composable
fun DatePickerDialog(
    onDateSelected: (day: Int, month: Int, year: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val weeks = (1..4).toList()
    val months = (1..12).toList()
    val years = (1900..Calendar.getInstance().get(Calendar.YEAR)).toList()

    var selectedWeek by remember { mutableStateOf(2) }
    var selectedMonth by remember { mutableStateOf(2) }
    var selectedYear by remember { mutableStateOf(2023) }

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
                    WheelPicker(items = weeks, selectedItem = selectedWeek) { selectedWeek = it }
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
                            selectedWeek,
                            selectedMonth,
                            selectedYear
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

// 날짜 선택 Wheel
@Composable
fun <T> WheelPicker(items: List<T>, selectedItem: T, onItemSelected: (T) -> Unit) {
    val paddedItems = listOf(null) + items + listOf(null)
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 초기값 자동 선택
    LaunchedEffect(Unit) {
        val index = paddedItems.indexOf(selectedItem) - 1
        if (index >= 0) {
            listState.scrollToItem(index)
        }
    }

    // Detect when the scroll stops and center the closest item
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerItemIndex = listState.layoutInfo.visibleItemsInfo
                .minByOrNull { Math.abs(it.offset + it.size / 2 - listState.layoutInfo.viewportEndOffset / 2) }
                ?.index ?: 0

            paddedItems[centerItemIndex]?.let {
                if (it != selectedItem) {
                    onItemSelected(it)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(114.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(paddedItems.size) { index ->
                paddedItems[index]?.let { item ->
                    Text(
                        text = item.toString(),
                        fontSize = if (item == selectedItem) 20.sp else 16.sp,
                        fontWeight = if (item == selectedItem) FontWeight.Bold else FontWeight.Normal,
                        color = if (item == selectedItem) Color.Black else Color.Gray,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable { onItemSelected(item) }
                    )
                } ?: Spacer(modifier = Modifier.height(32.dp))
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -19.dp),
            thickness = 2.dp,
            color = Color.Black
        )

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 19.dp),
            thickness = 2.dp,
            color = Color.Black
        )
    }
}
