package com.eeos.rocatrun.stats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.eeos.rocatrun.ui.theme.RoCatRunTheme


class StatsActivity : ComponentActivity() {

    private val statsViewModel: StatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        statsViewModel.fetchDailyStats()  // API 데이터 호출

        setContent {
            RoCatRunTheme {
                StatsScreen(statsViewModel = statsViewModel)
            }
        }
    }
}
