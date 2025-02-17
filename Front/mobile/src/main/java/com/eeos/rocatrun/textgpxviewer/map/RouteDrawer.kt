package com.eeos.rocatrun.textgpxviewer.map

import android.graphics.BitmapFactory
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.eeos.rocatrun.R
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.sources.addSource

fun addRouteToMap(mapView: MapView, points: List<Point>) {
    if (points.isEmpty()) {
        Log.e("GPX_DEBUG", "❌ 지도에 추가할 경로가 없음.")
        return
    }

    val mapboxMap = mapView.getMapboxMap()
    val lineString = LineString.fromLngLats(points)

    mapboxMap.getStyle { style ->
        val routeSourceId = "gpx-route-source"
        val routeLayerId = "gpx-route-layer"
        val markerSourceId = "route-markers"
        val startLayerId = "start-marker"
        val endLayerId = "end-marker"

        // 기존 소스 및 레이어 삭제 (중복 방지)
        if (style.styleSourceExists(routeSourceId)) {
            style.removeStyleSource(routeSourceId)
        }
        if (style.styleLayerExists(routeLayerId)) {
            style.removeStyleLayer(routeLayerId)
        }
        if (style.styleSourceExists(markerSourceId)) {
            style.removeStyleSource(markerSourceId)
        }
        if (style.styleLayerExists(startLayerId)) {
            style.removeStyleLayer(startLayerId)
        }
        if (style.styleLayerExists(endLayerId)) {
            style.removeStyleLayer(endLayerId)
        }

        // 경로 소스 추가
        val routeSource = geoJsonSource(routeSourceId) {
            featureCollection(FeatureCollection.fromFeature(Feature.fromGeometry(lineString)))
        }
        style.addSource(routeSource)

        // 경로 스타일 적용
        val routeLayer = lineLayer(routeLayerId, routeSourceId) {
            lineColor("#1E90FF") // 경로 색상 (파란색)
            lineWidth(3.0) // 경로 두께
        }
        style.addLayer(routeLayer)

        // 출발점 & 도착점 추가
        val startPoint = points.first()
        val endPoint = points.last()
        val startFeature = Feature.fromGeometry(startPoint)
        val endFeature = Feature.fromGeometry(endPoint)

        val markerSource = geoJsonSource(markerSourceId) {
            featureCollection(FeatureCollection.fromFeatures(listOf(startFeature, endFeature)))
        }
        style.addSource(markerSource)

        // 아이콘 로드 (res/drawable/ 폴더에서 불러옴)
        val context = mapView.context
        val startIconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.all_img_whitecat)
        val endIconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.all_img_whitecat)

        style.addImage("start-icon", startIconBitmap)
        style.addImage("end-icon", endIconBitmap)

        // 출발점 아이콘 레이어
        val startIconLayer = symbolLayer(startLayerId, markerSourceId) {
            iconImage("start-icon")
            iconSize(0.1) // 아이콘 크기 조정
            iconOffset(listOf(0.0, -0.5)) // 위치 조정
        }
        style.addLayer(startIconLayer)

        // 도착점 아이콘 레이어
        val endIconLayer = symbolLayer(endLayerId, markerSourceId) {
            iconImage("end-icon")
            iconSize(0.1)
            iconOffset(listOf(0.0, -0.5))
        }
        style.addLayer(endIconLayer)
    }

    // 🔹 초기 카메라 설정: 전체 경로가 보이도록 설정
//    if (points.isNotEmpty()) {
//        val firstPoint = points.first()
//        val lastPoint = points.last()
//
//        val bounds = com.mapbox.maps.CameraOptions.Builder()
//            .center(Point.fromLngLat(
//                (firstPoint.longitude() + lastPoint.longitude()) / 2,
//                (firstPoint.latitude() + lastPoint.latitude()) / 2
//            )) // 경로 중앙으로 카메라 이동
//            .zoom(14.0) // 적절한 줌 레벨 (경로 전체가 보이도록)
//            .build()
//        mapboxMap.setCamera(bounds)
//    }
    if (points.isNotEmpty()) {
        // 모든 경로가 보이도록 패딩을 설정합니다.
        val padding = com.mapbox.maps.EdgeInsets(50.0, 50.0, 50.0, 50.0)
        // 내장 함수를 이용해 포인트들을 모두 포함하는 카메라 옵션 계산
        val cameraOptions = mapboxMap.cameraForCoordinates(points, padding, 0.0, 0.0)
        mapboxMap.setCamera(cameraOptions)
    }

    // 🔹 줌, 스크롤, 회전 완전 비활성화 (사용자가 조작할 수 없음)
    mapView.gestures.updateSettings {
//        scrollEnabled = false  // 🔹 줌 기능 비활성화
        rotateEnabled = false // 🔹 스크롤 비활성화
        pinchScrollEnabled = false // 🔹 회전 비활성화
    }

    // 🔹 터치 이벤트도 무시하여 인터랙션 방지 (지도 클릭해도 아무 반응 없게)
//    mapView.setOnTouchListener { _, _ -> true }
}
