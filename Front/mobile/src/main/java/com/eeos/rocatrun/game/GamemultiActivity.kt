package com.eeos.rocatrun.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.core.content.FileProvider
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import com.google.android.gms.wearable.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GameMulti : ComponentActivity(), DataClient.OnDataChangedListener {
    private lateinit var dataClient: DataClient
    private var runningData by mutableStateOf<RunningData?>(null)
    private var gpxFileReceived by mutableStateOf(false)

    data class RunningData(
        val averagePace: Double,
        val totalDistance: Double,
        val elapsedTime: Long,
        val heartRate: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataClient = Wearable.getDataClient(this)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
        )

        setContent {
            RoCatRunTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GamemultiScreen(
                        runningData = runningData,
                        gpxFileReceived = gpxFileReceived,
                        onShareClick = { shareLatestGpxFile() }
                    )
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                when (dataItem.uri.path) {
                    "/running_data" -> processRunningData(dataItem)
                    "/gpx_data" -> processGpxData(dataItem)
                }
            }
        }
    }

    private fun processRunningData(dataItem: DataItem) {
        DataMapItem.fromDataItem(dataItem).dataMap.apply {
            runningData = RunningData(
                averagePace = getDouble("pace"),
                totalDistance = getDouble("distance"),
                elapsedTime = getLong("time"),
                heartRate = getString("heartRate", "--")
            )
        }
    }

    private fun processGpxData(dataItem: DataItem) {
        val asset = DataMapItem.fromDataItem(dataItem).dataMap.getAsset("gpx_file")
        asset?.let {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = Wearable.getDataClient(this@GameMulti).getFdForAsset(it).await()
                    response.inputStream?.use { stream ->
                        val gpxContent = stream.bufferedReader().use { it.readText() }
                        saveGpxFile(gpxContent)
                        gpxFileReceived = true
                    }

                } catch (e: IOException) {
                    Log.e("GameMulti", "Error processing GPX data", e)
                }
            }
        }
    }

    private fun saveGpxFile(content: String) {
        try {
            val file = File(getExternalFilesDir(null), "activity_${System.currentTimeMillis()}.gpx")
            FileOutputStream(file).use { it.write(content.toByteArray()) }
            Log.d("GameMulti", "GPX file saved: ${file.absolutePath}")
        } catch (e: IOException) {
            Log.e("GameMulti", "Error saving GPX file", e)
        }
    }

    private fun shareLatestGpxFile() {
        val directory = getExternalFilesDir(null)
        val gpxFiles = directory?.listFiles { file -> file.name.endsWith(".gpx") }

        gpxFiles?.maxByOrNull { it.lastModified() }?.let { latestFile ->
            shareGpxFile(this, latestFile.name)
        } ?: run {
            // GPX 파일이 없을 경우 처리
            Log.e("GameMulti", "No GPX files found to share")
        }
    }

    private fun shareGpxFile(context: Context, fileName: String) {
        val file = File(context.getExternalFilesDir(null), fileName)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/gpx+xml"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "GPX 파일 공유"))
    }
}