package com.eeos.rocatrun.login.util

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.Paint.Align
import android.graphics.Paint.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.paint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.eeos.rocatrun.R
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.intro.IntroActivity


@Composable
fun MessageBox(
    imageResId: Int,
    message: String,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,  // 박스를 중앙에 정렬
        modifier = modifier
            .fillMaxSize()  // 전체 화면 크기 채우기
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(600.dp)
                .height(600.dp)
                .padding(16.dp)
        ) {
            // 배경 이미지
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxWidth().height(300.dp)

            )

            // 메시지 및 확인 텍스트 표시
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = message,
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(R.font.neodgm))
                    ),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .offset(y = 30.dp)
                )

                // 터치 가능한 텍스트 (확인 버튼 역할)
                Text(
                    text = "확인",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(R.font.neodgm)),
                        textDecoration = TextDecoration.Underline  // 밑줄 추가
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .offset(y = 50.dp)
                        .clickable {
                            val intent = Intent(context, IntroActivity::class.java)
                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}