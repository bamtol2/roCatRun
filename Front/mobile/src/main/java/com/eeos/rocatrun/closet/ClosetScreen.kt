package com.eeos.rocatrun.closet


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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import com.eeos.rocatrun.ui.components.GifImage
import com.eeos.rocatrun.ui.components.StrokedText
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController


@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun ClosetScreen(closetViewModel: ClosetViewModel) {
    val context = LocalContext.current
    val token = TokenStorage.getAccessToken(context)

    // 아이템 목록
    val itemList = closetViewModel.itemList.value

    // 이미지 캡쳐 변수
    val captureController = rememberCaptureController()
    var showSaveCheck by remember { mutableStateOf(false) }

    // 탭
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("전체", "물감", "머리띠", "풍선", "오라")

    // 리스트로 만들어서 착용 아이템들 전달하는 용도로 사용해도 될 듯
    var equippedItem by remember { mutableStateOf<String?>(null) }

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
                    CharacterWithItems(wornItems = itemList.filter { it.equipped })
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
                            0 -> itemList // 전체
                            1 -> itemList.filter { it.category == "PAINT" } // 물감
                            2 -> itemList.filter { it.category == "HEADBAND" } // 머리띠
                            3 -> itemList.filter { it.category == "BALLOON" } // 풍선
                            4 -> itemList.filter { it.category == "AURA" } // 오라
                            else -> emptyList()
                        }


                        items(
                            items = currentItems,
                            key = { item -> item.inventoryId }
                        ) { item ->
                            ItemCard(
                                item = item,
                                onClick = { closetViewModel.toggleItemEquipped(it) }
                            )
                        }

                    }

                    // 획득 아이템 표시 텍스트
                    val currentItemCount = when (selectedTab) {
                        0 -> itemList.size // 전체
                        1 -> itemList.count { it.category == "PAINT" }
                        2 -> itemList.count { it.category == "HEADBAND" }
                        3 -> itemList.count { it.category == "BALLOON" }
                        4 -> itemList.count { it.category == "AURA" }
                        else -> 0
                    }

                    StrokedText(
                        text = "획득 아이템 : 총 ${currentItemCount}개",
                        fontSize = 14,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(vertical = 5.dp, horizontal = 30.dp)
                            .offset(x = 195.dp, y = 285.dp),
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
@Composable
fun ItemCard(item: InventoryItem, onClick: (InventoryItem) -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp)
            .background(color = Color(0x80FFB9C7), shape = RoundedCornerShape(size = 18.dp))
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
                if (item.listImageIsGif && item.listImage != null) {
                    GifImage(
                        modifier = Modifier.size(60.dp),
                        gifUrl = "android.resource://com.eeos.rocatrun/${item.listImage}"
                    )
                } else {
                    item.listImage?.let {
                        Image(
                            painter = rememberAsyncImagePainter(item.listImage),
                            contentDescription = "착용 아이템",
                            modifier = Modifier
                                .size(200.dp)
                        )
                    }
                }
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
@Composable
fun CharacterWithItems(wornItems: List<InventoryItem>) {
    // 이미지 캡쳐 사이즈 200.dp
    Box(modifier = Modifier.size(300.dp)) {
        // 특정 카테고리(예: "오라")의 아이템 배치 (캐릭터 뒤)
        wornItems.filter { it.category == "AURA" }.forEach { item ->
            if (item.equipImageIsGif && item.equipImage != null) {
                GifImage(
                    modifier = Modifier
                        .size(300.dp),
                    gifUrl = "android.resource://com.eeos.rocatrun/${item.equipImage}"
                )
            } else {
                item.equipImage?.let {
                    Image(
                        painter = rememberAsyncImagePainter(item.equipImage),
                        contentDescription = "착용 아이템",
                        modifier = Modifier
                            .size(300.dp)
                    )
                }
            }
        }

        // 캐릭터 이미지 (물감 적용하기 PAINT)
        GifImage(
            modifier = Modifier
                .size(300.dp),
            gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.color_white_on}"
        )

        // 나머지 카테고리의 아이템 배치 (캐릭터 위)
        wornItems.filter { it.category != "AURA" }.forEach { item ->
            if (item.equipImageIsGif && item.equipImage != null) {
                // GIF 이미지
                GifImage(
                    modifier = Modifier
                        .size(300.dp),
                    gifUrl = "android.resource://com.eeos.rocatrun/${item.equipImage}"
                )
            } else {
                // 일반 이미지
                item.equipImage?.let {
                    Image(
                        painter = rememberAsyncImagePainter(item.equipImage),
                        contentDescription = "착용 아이템",
                        modifier = Modifier
                            .size(300.dp)
                    )
                }
            }
        }
    }
}