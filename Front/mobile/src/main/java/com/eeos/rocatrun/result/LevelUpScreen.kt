package com.eeos.rocatrun.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eeos.rocatrun.R
import com.eeos.rocatrun.ui.components.StrokedText

@Composable
fun LevelUpScreen(
    oldLevel: Int,
    newLevel: Int,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,  // 박스를 중앙에 정렬
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.login_img_check),
                contentDescription = "Modal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {

                StrokedText(
                    text = "Level Up!",
                    fontSize = 25,
                    color = Color.White,
                    strokeWidth = 15f,
                    strokeColor = Color(0xFFA8B6FD)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Lv.${oldLevel} → Lv.${newLevel}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        color = Color.White
                    )
                )

                Text(
                    text = "확인",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .offset(y = 33.dp)
                        .clickable {
                            onDismiss()
                        }
                )
            }
        }
    }
}