package com.eeos.rocatrun.shop

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.eeos.rocatrun.R
import com.eeos.rocatrun.closet.CustomTabRow
import com.eeos.rocatrun.closet.api.InventoryItem
import com.eeos.rocatrun.game.AlertScreen
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.shop.api.ShopViewModel
import com.eeos.rocatrun.ui.components.StrokedText


@Composable
fun ShopScreen(shopViewModel: ShopViewModel) {
    val context = LocalContext.current
    val token = TokenStorage.getAccessToken(context)

    // 전체 인벤토리, 선택 리스트, 총 금액
    val allInventoryList = shopViewModel.allInventoryList.value
    val selectedItems = shopViewModel.selectedItems.value
    val totalPrice = shopViewModel.totalPrice.value

    // 탭
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("전체", "물감", "머리띠", "풍선", "오라")
    
    // 모달 변수
    var showAlert by remember { mutableStateOf(false) }
    var showEmptySelectionAlert by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.shop_bg_image),
            contentDescription = "shop background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // 홈 버튼
        Button(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 20.dp, y = 60.dp)
                .padding(10.dp),
            onClick = {
                val intent = Intent(context, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(0.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.stats_icon_home),
                contentDescription = "Home Icon",
                modifier = Modifier.size(50.dp)
            )
        }


        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .offset(y = 80.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "선택 아이템 : 총 ${selectedItems.size}개",
                    color = Color.White,
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "획득 가능 캔코인 : $totalPrice",
                    color = Color.White,
                    fontSize = 20.sp,
                )

                // 판매 버튼
                Button(
                    onClick = {
                        if (selectedItems.isEmpty()) {
                            showEmptySelectionAlert = true
                        } else {
                            showAlert = true
                            shopViewModel.postSellItem(token, selectedItems, totalPrice)
                            shopViewModel.fetchAllInventoryShop(token)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shop_btn_img),
                        contentDescription = "Sell btn",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            // 탭과 아이템 목록 영역
            Column(
                modifier = Modifier
                    .weight(1f)
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
                            0 -> allInventoryList // 전체
                            1 -> allInventoryList.filter { it.category == "PAINT" }
                            2 -> allInventoryList.filter { it.category == "HEADBAND" }
                            3 -> allInventoryList.filter { it.category == "BALLOON" }
                            4 -> allInventoryList.filter { it.category == "AURA" }
                            else -> emptyList()
                        }

                        // 아이템 선택 시 리스트에 자동 추가, 제거
                        items(currentItems) { item ->
                            ShopItemCard(
                                item = item,
                                isSelected = selectedItems.contains(item.inventoryId),
                                onBoxClick = {
                                    shopViewModel.toggleItemSelection(
                                        item.inventoryId,
                                        item.price,
                                    )
                                }
                            )
                        }
                    }

                    // 획득 아이템 표시 텍스트
                    val currentItemCount = when (selectedTab) {
                        0 -> allInventoryList.size
                        1 -> allInventoryList.count { it.category == "PAINT" }
                        2 -> allInventoryList.count { it.category == "HEADBAND" }
                        3 -> allInventoryList.count { it.category == "BALLOON" }
                        4 -> allInventoryList.count { it.category == "AURA" }
                        else -> 0
                    }

                    StrokedText(
                        text = "획득 아이템 : 총 ${currentItemCount}개",
                        fontSize = 14,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(vertical = 5.dp, horizontal = 30.dp)
                            .offset(y = 490.dp),
                    )
                }
            }
        }

        if (showAlert) {
            AlertScreen(
                message = "판매완료다냥!",
                onDismissRequest = {showAlert = false}
            )
        }

        if (showEmptySelectionAlert) {
            AlertScreen(
                message = "선택한 아이템이 없다냥!",
                onDismissRequest = {showAlert = false}
            )
        }
    }
}


// Item UI
@SuppressLint("DiscouragedApi")
@Composable
fun ShopItemCard(item: InventoryItem, isSelected: Boolean, onBoxClick: () -> Unit) {
    val context = LocalContext.current
    val boxColor = when {
        item.equipped -> Color(0xD855454F)
        isSelected -> Color(0xFFFBF8C5)
        else -> Color(0x80FFB9C7)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp)
            .background(color = boxColor, shape = RoundedCornerShape(size = 18.dp))
            .then(
                if (!item.equipped) Modifier.clickable { onBoxClick() }
                else Modifier  // 장착된 아이템은 클릭 불가
            )
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
                    modifier = Modifier.size(200.dp)
                )

            }

            // 선택/해제 버튼
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(33.dp)
                    .clickable {
                        if (item.equipped) {
                            Toast.makeText(context, "장착된 아이템은 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            onBoxClick()
                        }
                    }
            ) {
                Image(
                    painter = painterResource(
                        id = if (isSelected) R.drawable.closet_btn_dresson
                        else R.drawable.closet_btn_dressoff
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )
                Text(
                    text = if (item.equipped) "장착 중" else if (isSelected) "해제" else "선택",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}