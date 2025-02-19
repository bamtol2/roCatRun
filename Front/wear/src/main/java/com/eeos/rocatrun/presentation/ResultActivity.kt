package com.eeos.rocatrun.presentation

import android.util.Log
import android.os.Bundle
import android.os.Build
import androidx.annotation.RequiresApi
import android.net.Uri
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.eeos.rocatrun.R
//import com.google.android.gms.wearable.MessageClient
//import com.google.android.gms.wearable.Wearable
//import android.content.Context
//import android.widget.Toast

class ResultActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen2()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        finishAndRemoveTask()
    }

}
@Composable
fun SplashScreen2() {
    val context = LocalContext.current
    val resultActivity = context as ResultActivity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))  // 상단 여백

        Text(
            text = "정복 완료!",
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.neodgm))
            ),
            textAlign = TextAlign.Center
        )
        // 고양이 이미지 추가
        // 이겼을 때, wear_img_wincat (정복 완료!) 졌을 때, wear_img_losecat (정복 실패..) 으로 바꿔줘야함
        Image(
            painter = painterResource(id = R.drawable.wear_img_wincat),
            contentDescription = "Victory Cat",
            modifier = Modifier
                .size(80.dp)  // 워치 화면에 맞는 크기
        )
        Text(
            text = "폰으로 이동해서\n결과를 확인하세요",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.neodgm)),
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 확인 버튼
        Button(
            onClick = {
                resultActivity.finish()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FFCC)
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(69.dp)
                .height(34.dp)
                .padding(horizontal = 2.dp)
        ) {
            Text(
                text = "확인",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.neodgm))
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false  // 줄바꿈 방지
            )
        }
    }
}