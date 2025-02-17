package com.eeos.rocatrun.closet

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.eeos.rocatrun.R
import com.eeos.rocatrun.closet.api.ClosetViewModel
import com.eeos.rocatrun.closet.api.Item
import com.eeos.rocatrun.ui.components.StrokedText

@Composable
fun GradeInfoScreen(onDismiss: () -> Unit, closetViewModel: ClosetViewModel) {
    // 아이템 목록
    val itemList = closetViewModel.itemList.value

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
                .height(800.dp)
                .border(
                    width = 3.dp,
                    color = Color(0xFF00E2B1)
                )
                .background(color = Color(0xE1000000))
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
                        text = "아이템 정보",
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StrokedText(
                        text = "등급별 획득 확률",
                        fontSize = 26,
                        color = Color.Black,
                        strokeWidth = 15f,
                        strokeColor = Color(0xFF00E2B1),
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    RarityItem("NORMAL", "일반냥", "40%", Color.White, Color(0xFFA3A1A5))
                    RarityItem("RARE", "레어냥", "30%", Color(0xFF018F2C), Color.White)
                    RarityItem("EPIC", "에픽냥", "20%", Color(0xFF6C13E1), Color.White)
                    RarityItem("UNIQUE", "유니크냥", "4%", Color(0xFF1646CB), Color.White)
                    RarityItem("LEGENDARY", "레전드리냥", "1%", Color(0xFFFF0080), Color(0xFFFFFF00))
                    Spacer(modifier = Modifier.height(10.dp))

                    StrokedText(
                        text = "아이템 종류",
                        fontSize = 26,
                        color = Color.Black,
                        strokeWidth = 15f,
                        strokeColor = Color(0xFF00E2B1),
                    )

                    ItemCategories(itemList)
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


@Composable
fun ItemCategories(itemList: List<Item>) {
    val groupedItems = itemList.groupBy { it.category }
    val categoryNames = mapOf(
        "BALLOON" to "풍선",
        "AURA" to "오라",
        "HEADBAND" to "머리띠",
        "PAINT" to "물감"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        groupedItems.forEach { (category, items) ->
            val categoryTitle = "${categoryNames[category] ?: category} - ${items.size}개"
            CategoryRow(categoryTitle, items)
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun CategoryRow(categoryTitle: String, itemList: List<Item>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(Color(0xFF1A3B2A), RoundedCornerShape(10.dp))
    ) {
        Text(
            text = categoryTitle,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
        ) {
            LazyRow() {
                items(itemList) { item ->
                    val resourceId =
                        context.resources.getIdentifier(
                            "${item.name}_off",
                            "drawable",
                            context.packageName
                        )
                    val imageUrl = if (resourceId != 0) {
                        "android.resource://${context.packageName}/$resourceId"
                    } else {
                        "android.resource://com.eeos.rocatrun/${R.drawable.closet_img_x}"
                    }

                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "아이템",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }


        }
    }
}