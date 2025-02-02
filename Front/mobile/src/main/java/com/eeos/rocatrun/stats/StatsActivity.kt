package com.eeos.rocatrun.stats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eeos.rocatrun.ui.theme.RoCatRunTheme


class StatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            RoCatRunTheme {
                StatsScreen()
            }
        }
    }
}
