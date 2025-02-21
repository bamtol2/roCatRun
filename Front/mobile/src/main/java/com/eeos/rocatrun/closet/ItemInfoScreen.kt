package com.eeos.rocatrun.closet

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.F
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.eeos.rocatrun.R
import com.eeos.rocatrun.closet.api.ClosetViewModel
import com.eeos.rocatrun.closet.api.InventoryItem
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.ui.components.StrokedText
import com.eeos.rocatrun.ui.theme.MyFontFamily
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("DiscouragedApi")
@Composable
fun ItemInfoScreen(
    item: InventoryItem,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    // 카테고리 한글 이름
    val categoryName = when (item.category) {
        "AURA" -> "오라"
        "PAINT" -> "물감"
        "HEADBAND" -> "머리띠"
        "BALLOON" -> "풍선"
        else -> "기타"
    }

    // rarity에 따른 색, 배경 설정
    val rarityStrokeColor = when (item.rarity) {
        "NORMAL" -> Color(0xFFFFFFFF)
        "RARE" -> Color(0xFF018F2C)
        "UNIQUE" -> Color(0xFF1646CB)
        "EPIC" -> Color(0xFF6C13E1)
        "LEGENDARY" -> Color(0xFFFF0080)
        else -> Color(0xFFFFFFFF)
    }

    val rarityTextColor = when (item.rarity) {
        "NORMAL" -> Color(0xFFA3A1A5)
        else -> Color.White
    }

    val bgResourceId = context.resources.getIdentifier(
        "closet_bg_iteminfo_${item.rarity.lowercase()}",
        "drawable",
        context.packageName
    )
    val bgImageUrl = if (bgResourceId != 0) {
        "android.resource://${context.packageName}/$bgResourceId"
    } else {
        "android.resource://com.eeos.rocatrun/${R.drawable.closet_bg_iteminfo_normal}"
    }


    // UI
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            // 배경 이미지
            Image(
                painter = rememberAsyncImagePainter(bgImageUrl),
                contentDescription = "Modal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )

            // 내용
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(26.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "아이템 : ${item.koreanName}",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.width(200.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text(
                            text = "카테고리 : $categoryName",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "등급 : ${item.rarity}",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "희귀 확률 : 0.2%",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    // 아이템 이미지
                    val itemRsourceId =
                        context.resources.getIdentifier(
                            "${item.name}_off",
                            "drawable",
                            context.packageName
                        )
                    val itemImageUrl = if (itemRsourceId != 0) {
                        "android.resource://${context.packageName}/$itemRsourceId"
                    } else {
                        "android.resource://com.eeos.rocatrun/${R.drawable.closet_img_x}"
                    }

                    Image(
                        painter = rememberAsyncImagePainter(itemImageUrl),
                        contentDescription = "아이템 이미지",
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "\"${item.description}\"",
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontFamily = MyFontFamily,
                        lineHeight = 22.sp,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }

            // 확인 버튼
            StrokedText(
                text = "확인",
                fontSize = 16,
                color = rarityTextColor,
                strokeColor = rarityStrokeColor,
                modifier = Modifier
                    .offset(y = 140.dp)
                    .padding(vertical = 4.dp)
                    .clickable { onDismissRequest() }
            )
        }
    }

}