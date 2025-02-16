package com.eeos.rocatrun.closet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.eeos.rocatrun.R
import com.eeos.rocatrun.ui.components.StrokedText

@Composable
fun GradeInfoScreen(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(310.dp)
                .border(
                    width = 3.dp,
                    color = Color(0xFF00E2B1)
                )
                .background(color = Color(0xB2000000))
                .wrapContentSize(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                // 헤더
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF00E2B1))
                        .height(50.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.game_icon_close),
                        contentDescription = "Close",
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 10.dp)
                            .size(32.dp)
                            .clickable { onDismiss() }
                    )

                    Text(
                        text = "아이템 획득 확률",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.Black,
                            fontSize = 28.sp,
                        ),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }

                // 내용
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RarityItem("NORMAL", "일반냥", "45%",  Color.White, Color(0xFFA3A1A5))
                    RarityItem("RARE", "레어냥", "38%", Color(0xFF018F2C), Color.White)
                    RarityItem("UNIQUE", "에픽냥", "28%", Color(0xFF1646CB), Color.White)
                    RarityItem("EPIC", "유니크냥", "4%", Color(0xFF6C13E1), Color.White)
                    RarityItem("LEGENDARY", "레전드리냥", "1%", Color(0xFFFF0080), Color(0xFFFFFF00))
                }
            }
        }
    }
}


@Composable
private fun RarityItem(
    rarity: String,
    name: String,
    percentage: String,
    strokeColor: Color,
    textColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .wrapContentSize(Alignment.Center)
        ) {
            StrokedText(
                text = rarity,
                fontSize = 24,
                color = textColor,
                strokeWidth = 15f,
                strokeColor = strokeColor,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .width(150.dp)
                .wrapContentSize(Alignment.CenterStart)
        ) {
            Text(
                text = "- $name $percentage",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}