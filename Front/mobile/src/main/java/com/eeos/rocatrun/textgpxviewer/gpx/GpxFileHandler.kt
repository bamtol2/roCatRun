package com.eeos.rocatrun.textgpxviewer.gpx

import android.content.Context
import com.mapbox.geojson.Point
import org.w3c.dom.Document
import java.io.File
import android.util.Log
import javax.xml.parsers.DocumentBuilderFactory

object GpxFileHandler {

    fun getGpxFileList(context: Context): List<File> {
        val directory = context.getExternalFilesDir(null) ?: return emptyList()
        return directory.listFiles { file -> file.extension == "gpx" }?.toList() ?: emptyList()
    }

    fun loadGpxFile(file: File): String? {
        return try {
            if (file.exists()) {
                val content = file.readText()
                if (content.contains("<trkpt")) {
                    Log.d("GPX_DEBUG", "✅ GPX 파일 정상 로드: ${file.absolutePath}")
                    content
                } else {
                    Log.w("GPX_DEBUG", "⚠️ GPX 파일에 경로 데이터 없음: ${file.absolutePath}")
                    null
                }
            } else {
                Log.e("GPX_DEBUG", "❌ GPX 파일이 존재하지 않음: ${file.absolutePath}")
                null
            }
        } catch (e: Exception) {
            Log.e("GPX_DEBUG", "❌ GPX 파일을 읽는 중 오류 발생: ${e.message}")
            null
        }
    }

    fun parseGpxFile(gpxContent: String?): List<Point> {
        val points = mutableListOf<Point>()

        if (gpxContent.isNullOrEmpty()) {
            println("GPX 파일 내용이 비어 있음.")
            return emptyList()
        }

        return try {
            val doc: Document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(gpxContent.byteInputStream())

            val nodeList = doc.getElementsByTagName("trkpt")

            if (nodeList.length == 0) {
                println("⚠️ GPX 파일에 유효한 위도/경도 데이터가 없음.")
                return emptyList()
            }

            for (i in 0 until nodeList.length) {
                val element = nodeList.item(i) as org.w3c.dom.Element
                val latStr = element.getAttribute("lat")
                val lonStr = element.getAttribute("lon")

                // 위도, 경도가 없는 경우 예외 처리
                if (latStr.isNullOrEmpty() || lonStr.isNullOrEmpty()) {
                    println("⚠️ GPX 파일의 일부 데이터에서 위도/경도가 누락됨. 건너뜀.")
                    continue
                }

                val lat = latStr.toDoubleOrNull()
                val lon = lonStr.toDoubleOrNull()

                if (lat != null && lon != null) {
                    points.add(Point.fromLngLat(lon, lat))
                } else {
                    println("⚠️ GPX 파일의 위도/경도 형식이 잘못됨. 건너뜀.")
                }
            }
            points
        } catch (e: Exception) {
            println("GPX 파일 파싱 중 오류 발생: ${e.message}")
            emptyList()
        }
    }
}
