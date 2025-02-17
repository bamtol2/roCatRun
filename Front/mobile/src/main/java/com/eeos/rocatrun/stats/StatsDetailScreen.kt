package com.eeos.rocatrun.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eeos.rocatrun.R
import com.eeos.rocatrun.stats.api.GameDetails
import com.eeos.rocatrun.textgpxviewer.gpx.GpxFileHandler
import com.eeos.rocatrun.textgpxviewer.map.MapboxGPXViewer
import com.eeos.rocatrun.ui.components.StrokedText
import com.eeos.rocatrun.ui.theme.MyFontFamily


@Composable
fun DetailDialog(date: String, details: GameDetails,recordIndex: Int, onDismiss: () -> Unit) {
    val dateWithoutTime = date.substringBefore("T").replace("-", "/")
    val context = LocalContext.current
    // 로컬 저장소에서 GPX 파일 목록을 가져옴
    val gpxFiles = remember { GpxFileHandler.getGpxFileList(context) }
    // 기록 순서에 맞는 GPX 파일 선택
    val selectedGpxFile = if (recordIndex in gpxFiles.indices) gpxFiles[recordIndex] else null
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(650.dp)
                .padding(0.dp),
            contentAlignment = Alignment.Center,
        ) {
            // 모달 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.stats_bg_detail_image),
                contentDescription = "Modal Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 15.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 모달 Title (날짜)
                Text(
                    text = dateWithoutTime,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(30.dp))

                // 개인 기록 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatColumn(label = "페이스", value = details.pace)
                    StatColumn(label = "칼로리", value = "${details.calories}kcal")
                    StatColumn(label = "케이던스", value = "${details.cadence}spm")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatColumn(label = "거리", value = "${roundToFirstDecimal(details.distance)}spm")
                    StatColumn(label = "시간", value = details.runningTime)
                }
                Spacer(modifier = Modifier.height(30.dp))

                // 기존의 정적인 지도 이미지 대신 GPX 경로를 표시하는 지도 컴포저블 사용
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)) {
                    if (selectedGpxFile != null) {
                        MapboxGPXViewer(selectedFile = selectedGpxFile)
                    } else {
                        // GPX 파일이 없을 경우 기존 이미지로 대체하거나 에러 메시지 출력
                        Image(
                            painter = painterResource(id = R.drawable.stats_img_map),
                            contentDescription = "Route Map",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))

                // 확인 버튼
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = Color(0xFF36DBEB),
                            shape = RoundedCornerShape(15.dp)
                        ),
                ) {
                    Text(
                        text = "확인",
                        style = TextStyle(
                            fontFamily = MyFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 20.sp,
            color = Color.White,
            fontFamily = MyFontFamily
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(50.dp)
                .background(Color.Transparent)
                .border(2.dp, Color(0xFF36DBEB), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            StrokedText(
                text = value,
                color = Color.White,
                strokeColor = Color(0xFF34B4C0),
                fontSize = 20,
            )
        }
    }
}

