package com.eeos.rocatrun.closet


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.eeos.rocatrun.R
import com.eeos.rocatrun.closet.api.ClosetViewModel
import com.eeos.rocatrun.closet.api.InventoryItem
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.profile.ProfileDialog
import com.eeos.rocatrun.ui.components.GifImage
import com.eeos.rocatrun.ui.components.StrokedText
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController


@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun ClosetScreen(closetViewModel: ClosetViewModel) {
    val context = LocalContext.current
    val token = TokenStorage.getAccessToken(context)

    // 인벤토리 목록
    val inventoryList = closetViewModel.inventoryList.value

    // 이미지 캡쳐 변수
    val captureController = rememberCaptureController()
    var showSaveCheck by remember { mutableStateOf(false) }

    // 탭
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("전체", "물감", "머리띠", "풍선", "오라")

    var showInfoGrade by remember { mutableStateOf(false) }

    var showInfoItem by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.closet_bg_closet),
            contentDescription = "closet select background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // 홈 버튼
        Button(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 20.dp, y = 70.dp)
                .padding(10.dp),
            onClick = { showSaveCheck = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(0.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.stats_icon_home),
                contentDescription = "Ranking Icon",
                modifier = Modifier.size(50.dp)
            )
        }

        // 아이템 등급 정보 버튼
        Button(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-10).dp, y = 70.dp)
                .padding(10.dp),
            onClick = { showInfoGrade = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(0.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.game_icon_inform),
                contentDescription = "Ranking Icon",
                modifier = Modifier.size(45.dp)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 캐릭터 영역
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.capturable(captureController)) {
                    CharacterWithItems(wornItems = inventoryList.filter { it.equipped })
                }
            }

            // 탭과 아이템 목록 영역
            Column(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)

            ) {
                CustomTabRow(
                    tabs = tabs,
                    selectedTabIndex = selectedTab,
                    onTabSelected = { index -> selectedTab = index }
                )

                // 아이템 목록 + 개수 텍스트
                Box(modifier = Modifier.fillMaxSize()) {
                    // 아이템 목록 (LazyVerticalGrid)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .border(width = 4.dp, color = Color(0xFFFFB9C7))
                            .background(
                                color = Color(0xD6B9999F),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(top = 8.dp, bottom = 23.dp, start = 8.dp, end = 8.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val currentItems = when (selectedTab) {
                            0 -> inventoryList // 전체
                            1 -> inventoryList.filter { it.category == "PAINT" } // 물감
                            2 -> inventoryList.filter { it.category == "HEADBAND" } // 머리띠
                            3 -> inventoryList.filter { it.category == "BALLOON" } // 풍선
                            4 -> inventoryList.filter { it.category == "AURA" } // 오라
                            else -> emptyList()
                        }


                        items(currentItems) { item ->
                            ItemCard(
                                item = item,
                                onClick = { closetViewModel.toggleItemEquipped(it) },
                                onBoxClick = {
                                    selectedItem = item
                                    showInfoItem = true
                                }
                            )
                        }

                    }

                    // 획득 아이템 표시 텍스트
                    val currentItemCount = when (selectedTab) {
                        0 -> inventoryList.size // 전체
                        1 -> inventoryList.count { it.category == "PAINT" }
                        2 -> inventoryList.count { it.category == "HEADBAND" }
                        3 -> inventoryList.count { it.category == "BALLOON" }
                        4 -> inventoryList.count { it.category == "AURA" }
                        else -> 0
                    }

                    StrokedText(
                        text = "획득 아이템 : 총 ${currentItemCount}개",
                        fontSize = 14,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(vertical = 5.dp, horizontal = 30.dp)
                            .offset(y = 285.dp),
                    )
                }
            }
        }

        // 프로필 저장 모달 표시
        if (showSaveCheck) {
            if (token != null) {
                SaveCheckScreen(
                    message = "프로필로 저장할까냥?",
                    onDismissRequest = { showSaveCheck = false },
                    captureController = captureController,
                    closetViewModel = closetViewModel,
                    token = token
                )
            }
        }

        // 아이템 등급 정보 모달 표시
        if (showInfoGrade) {
            GradeInfoScreen(onDismiss = { showInfoGrade = false }, closetViewModel = closetViewModel)
        }

        // 아이템 정보 모달 표시
        if (showInfoItem && selectedItem != null) {
            ItemInfoScreen(
                item = selectedItem!!,
                onDismissRequest = { showInfoItem = false }
            )
        }
    }
}

// Custom Tab
@Composable
fun CustomTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedTabIndex

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isSelected) Color(0xFFFF6F8B) else Color(0xFFFFB9C7),
                        shape = RoundedCornerShape(topStartPercent = 50, topEndPercent = 50)
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                StrokedText(
                    text = title,
                    fontSize = 16,
                    strokeWidth = 10f,
                    color = Color.White,
                    strokeColor = Color(0xFF74313F)
                )
            }
        }
    }
}

// Item UI
@SuppressLint("DiscouragedApi")
@Composable
fun ItemCard(item: InventoryItem, onClick: (InventoryItem) -> Unit, onBoxClick: () -> Unit) {
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp)
            .background(color = Color(0x80FFB9C7), shape = RoundedCornerShape(size = 18.dp))
            .clickable { onBoxClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .padding(bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // 아이템 이미지
                val resourceId = context.resources.getIdentifier(
                    "${item.name}_off",
                    "drawable",
                    context.packageName
                )
                val imageRes = if (resourceId != 0) {
                    "android.resource://${context.packageName}/$resourceId"
                } else {
                    "android.resource://com.eeos.rocatrun/${R.drawable.closet_img_x}"
                }

                Image(
                    painter = rememberAsyncImagePainter(imageRes),
                    contentDescription = "착용 전 아이템",
                    modifier = Modifier
                        .size(200.dp)
                )

            }

            // 착용/해제 버튼
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(33.dp)
                    .clickable { onClick(item) }
            ) {
                Image(
                    painter = painterResource(
                        id = if (item.equipped) R.drawable.closet_btn_dresson
                        else R.drawable.closet_btn_dressoff
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )
                Text(
                    text = if (item.equipped) "해제" else "착용",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


// 아이템 장착 화면 - 상단 캐릭터 영역
@SuppressLint("DiscouragedApi")
@Composable
fun CharacterWithItems(wornItems: List<InventoryItem>) {
    val context = LocalContext.current

    // 이미지 캡쳐 사이즈 230.dp
    Box(
        modifier = Modifier
            .size(230.dp)
            .scale(1.3f)
    ) {
        // 특정 카테고리(예: "오라")의 아이템 배치 (캐릭터 뒤)
        wornItems.filter { it.category == "AURA" }.forEach { item ->
            val resourceId =
                context.resources.getIdentifier("${item.name}_on", "drawable", context.packageName)
            val imageUrl = if (resourceId != 0) {
                "android.resource://${context.packageName}/$resourceId"
            } else {
                "android.resource://com.eeos.rocatrun/${R.drawable.closet_img_x}"
            }

            if (item.isGif) {
                GifImage(
                    modifier = Modifier.size(300.dp),
                    gifUrl = imageUrl
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "착용 아이템",
                    modifier = Modifier.size(300.dp)
                )
            }
        }

        // 캐릭터 이미지
        val paintItem = wornItems.filter { it.category == "PAINT" }.firstOrNull()
        val imageUrl = paintItem?.let {
            val resourceId =
                context.resources.getIdentifier("${it.name}_on", "drawable", context.packageName)
            if (resourceId != 0) {
                "android.resource://${context.packageName}/$resourceId"
            } else {
                "android.resource://com.eeos.rocatrun/${R.drawable.color_white_on}"
            }
        } ?: "android.resource://com.eeos.rocatrun/${R.drawable.color_white_on}" // 기본 이미지

        GifImage(
            modifier = Modifier.size(300.dp),
            gifUrl = imageUrl
        )

        // 나머지 카테고리의 아이템 배치 (캐릭터 위)
        wornItems.filter { it.category != "AURA" && it.category != "PAINT" }.forEach { item ->
            val resourceId =
                context.resources.getIdentifier("${item.name}_on", "drawable", context.packageName)
            val imageUrl = if (resourceId != 0) {
                "android.resource://${context.packageName}/$resourceId"
            } else {
                "android.resource://com.eeos.rocatrun/${R.drawable.closet_img_x}"
            }

            if (item.isGif) {
                GifImage(
                    modifier = Modifier.size(300.dp),
                    gifUrl = imageUrl
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "착용 아이템",
                    modifier = Modifier.size(300.dp)
                )
            }
        }
    }
}