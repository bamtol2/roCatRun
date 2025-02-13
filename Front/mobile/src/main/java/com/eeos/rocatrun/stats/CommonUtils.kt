package com.eeos.rocatrun.stats

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.ui.components.StrokedText

// 거리 형식 반환 함수
fun roundToFirstDecimal(value: Double): Double {
    return "%,.1f".format(value).toDouble()
}

// 시간 형식 변환 함수
fun formatTotalTime(totalTime: String): String {
    val timeParts = totalTime.split(":")
    val hours = timeParts[0].padStart(2, '0') // 2자리로 맞추기 위해 '0' 채우기
    val minutes = timeParts[1].padStart(2, '0')

    return "${hours}h ${minutes}m"
}

// 날짜를 파싱하는 함수
fun parseYearMonthWeek(date: String): Triple<Int, Int, Int> {
    return try {
        val parts = date.split("년", "월", "주").map { it.trim() }
        if (parts.size < 3) {
            throw IllegalArgumentException("잘못된 날짜 포맷: $date")
        }
        val selectedYear = parts[0].toInt()
        val selectedMonth = parts[1].toInt()
        val selectedWeek = parts[2].toInt()
        Triple(selectedYear, selectedMonth, selectedWeek)
    } catch (e: Exception) {
        Log.e("parseYearMonthWeek", "잘못된 날짜 포맷: $date", e)
        Triple(0, 0, 0) // 기본값 반환
    }
}

fun parseYearMonth(date: String): Pair<Int, Int> {
    return try {
        val parts = date.split("년", "월").map { it.trim() }
        if (parts.size < 2) {
            throw IllegalArgumentException("잘못된 날짜 포맷: $date")
        }
        val selectedYear = parts[0].toInt()
        val selectedMonth = parts[1].toInt()
        Pair(selectedYear, selectedMonth)
    } catch (e: Exception) {
        Log.e("parseYearMonth", "잘못된 날짜 포맷: $date", e)
        Pair(0, 0) // 기본값 반환
    }
}


// 정보 디자인 함수
@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        StrokedText(
            text = value,
            color = Color.White,
            strokeColor = Color(0xFF34B4C0),
            fontSize = 35,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

// 날짜 선택 Wheel
@Composable
fun <T> WheelPicker(items: List<T>, selectedItem: T, onItemSelected: (T) -> Unit) {
    val paddedItems = listOf(null) + items + listOf(null)
    val listState = rememberLazyListState()

    // 초기값 자동 선택
    LaunchedEffect(Unit) {
        val index = paddedItems.indexOf(selectedItem) - 1
        if (index >= 0) {
            listState.scrollToItem(index)
        }
    }

    // 중심에서 근처 항목 위치 시 자동 선택
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

    // UI
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