package com.eeos.rocatrun.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable

class GameMulti : ComponentActivity() {
    private lateinit var dataClient: DataClient
    private var runningData by mutableStateOf<RunningData?>(null)

    data class RunningData(
        val averagePace: Double,
        val totalDistance: Double,
        val elapsedTime: Long,
        val heartRate: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataClient = Wearable.getDataClient(this)
        setupDataListener()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb()
            )
        )

        setContent {
            RoCatRunTheme(
                darkTheme = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GamemultiScreen(runningData)
                }
            }
        }
    }

    private fun setupDataListener() {
        dataClient.addListener { dataEvents ->
            dataEvents.forEach { event ->
                if (event.type == DataEvent.TYPE_CHANGED) {
                    val dataItem = event.dataItem
                    if (dataItem.uri.path == "/running_data") {
                        DataMapItem.fromDataItem(dataItem).dataMap.apply {
                            runningData = RunningData(
                                averagePace = getDouble("pace"),
                                totalDistance = getDouble("distance"),
                                elapsedTime = getLong("time"),
                                heartRate = getString("heartRate", "--"),

                            )
                        }
                    }
                }
            }
        }
    }
}

