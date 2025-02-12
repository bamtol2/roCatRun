package com.eeos.rocatrun.stats

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.stats.api.StatsViewModel
import com.eeos.rocatrun.ui.theme.RoCatRunTheme


class StatsActivity : ComponentActivity() {

    private val statsViewModel: StatsViewModel by viewModels()

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 초기값을 현재 날짜에 맞게 설정
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH) + 1
        val week = (currentDate.get(Calendar.WEEK_OF_MONTH))
        val dateString = String.format("%04d-%02d", year, month)

        // API 호출
        val token = TokenStorage.getAccessToken(this)

        statsViewModel.fetchDailyStats(token)
        statsViewModel.fetchWeekStats(token, dateString, week)
        statsViewModel.fetchMonStats(token, dateString)

        setContent {
            RoCatRunTheme {
                StatsScreen(statsViewModel = statsViewModel)
            }
        }
    }
}
