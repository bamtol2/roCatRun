package com.eeos.rocatrun.stats

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.eeos.rocatrun.stats.api.StatsViewModel
import com.eeos.rocatrun.ui.theme.RoCatRunTheme


class StatsActivity : ComponentActivity() {

    private val statsViewModel: StatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 초기값을 현재 날짜에 맞게 설정
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH) + 1
        val week = (currentDate.get(Calendar.WEEK_OF_MONTH))

        // API 호출
        statsViewModel.fetchDailyStats()
        statsViewModel.fetchWeekStats(year, month, week)
        statsViewModel.fetchMonStats(year, month)

        setContent {
            RoCatRunTheme {
                StatsScreen(statsViewModel = statsViewModel)
            }
        }
    }
}
