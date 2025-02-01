package com.eeos.rocatrun.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R

@Composable
fun LoginButton(text: String, borderColor: Color, backgroundColor: Color, iconResId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(270.dp)
            .height(50.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(9.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(9.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 16.dp)  // 양쪽 여백 설정
        ) {
            // 로고 이미지
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )

            // 로고와 텍스트 사이 간격
            Spacer(modifier = Modifier.width(12.dp))

            // 로그인 텍스트 (가운데 정렬을 위한 weight)
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.neodgm)),
                    color = Color.White
                ),
                modifier = Modifier
                    .weight(1f)  // 텍스트가 중앙에 위치하도록 설정
            )
        }
    }
}
