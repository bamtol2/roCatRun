package com.eeos.rocatrun.ppobgi

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.ui.components.StrokedText

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    showCoin: Boolean = false,
    coinAmount: String? = null,
    buttonHeight: Int = 200,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        // 버튼 배경
        Image(
            painter = painterResource(id = R.drawable.ppobgi_btn_random),
            contentDescription = "$text 버튼",
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight.dp)
        )

        // 텍스트와 코인 정보
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showCoin) {
                Image(
                    painter = painterResource(id = R.drawable.home_img_cancoin),
                    contentDescription = "코인 아이콘",
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StrokedText(
                    text = coinAmount ?: "",
                    fontSize = 30,
                    color = Color.White,
                    strokeColor = Color(0xFF7D376C)
                )
            } else {
                StrokedText(
                    text = text,
                    fontSize = 17,
                    color = Color.White,
                    strokeColor = Color(0xFF7D376C)
                )
            }
        }
    }
}