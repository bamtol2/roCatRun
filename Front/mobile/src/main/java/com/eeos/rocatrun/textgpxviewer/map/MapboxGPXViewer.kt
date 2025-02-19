package com.eeos.rocatrun.textgpxviewer.map

import android.util.Log
import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.eeos.rocatrun.textgpxviewer.gpx.GpxFileHandler
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import java.io.File
import java.util.Locale

@Composable
fun MapboxGPXViewer(selectedFile: File) {
    val context = LocalContext.current
    var points by remember { mutableStateOf<List<com.mapbox.geojson.Point>>(emptyList()) }
    var loadAttempted by remember { mutableStateOf(false) }

    LaunchedEffect(selectedFile) {
        val gpxContent = GpxFileHandler.loadGpxFile(selectedFile)
        if (!gpxContent.isNullOrEmpty()) {
            val parsedPoints = GpxFileHandler.parseGpxFile(gpxContent)
            if (parsedPoints.isNotEmpty()) {
                points = parsedPoints
            } else {
                Log.d("GPX", "⚠️ GPX 데이터에 경로가 없음.")
            }
        } else {
            Log.d("GPX", "⚠️ GPX 데이터를 불러오지 못함.")
        }
        loadAttempted = true
    }

    if (loadAttempted && points.isEmpty()) {
        // 경로 데이터가 없을 때
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "경로가 없습니다.",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    } else {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val mapView = MapView(ctx)
                mapView.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/dark-v10") { style ->
                    if (points.isNotEmpty()) {
                        addRouteToMap(mapView, points)
                    } else {
                        Log.d("GPX", "경로 데이터가 없으므로 지도에 표시하지 않음.")
                    }
                    mapView.scalebar.updateSettings {
                        enabled = false
                    }
                    style.localizeLabels(Locale.KOREAN)
                }
                mapView
            }
        )
    }
}
