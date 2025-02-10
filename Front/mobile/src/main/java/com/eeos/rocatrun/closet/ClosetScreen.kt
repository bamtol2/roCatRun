package com.eeos.rocatrun.closet

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.game.InfoScreen
import com.eeos.rocatrun.game.MainButtons
import com.eeos.rocatrun.game.TopNavigation
import com.eeos.rocatrun.home.HomeActivity


data class ItemPosition(
    val x: Int, // X 좌표 (dp 단위)
    val y: Int  // Y 좌표 (dp 단위)
)

data class WearableItem(
    val id: String,
    val imageRes: Int, // 이미지 리소스
    val position: ItemPosition, // 적용 위치
    val size: Int, // 아이템 크기
    val category: String,
    val categoryInt: Int,
    var isWorn: Boolean = false // 착용 여부
)


@Composable
fun ClosetScreen() {
    // 상단 홈 아이콘 버튼
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("전체", "물감", "머리띠", "풍선", "오라")
    var equippedItem by remember { mutableStateOf<String?>(null) }

    // 아이템 더미 리스트
    var items by remember {
        mutableStateOf(
            listOf(
                WearableItem("aura1", R.drawable.closet_ora_1, ItemPosition(10, 90), 300, "오라", 4),
                WearableItem("aura2", R.drawable.closet_ora_2, ItemPosition(10, -50), 300, "오라", 4),
                WearableItem(
                    "balloon1",
                    R.drawable.closet_balloon_1,
                    ItemPosition(70, -20),
                    80,
                    "풍선",
                    3
                ),
                WearableItem(
                    "balloon2",
                    R.drawable.closet_balloon_2,
                    ItemPosition(70, -20),
                    80,
                    "풍선",
                    3
                ),
            )
        )
    }

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
            onClick = { context.startActivity(Intent(context, HomeActivity::class.java)) },
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
                CharacterWithItems(wornItems = items.filter { it.isWorn })
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

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
                            .padding(8.dp), // 내부 여백 추가,
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val currentItems = when (selectedTab) {
                            0 -> items // 전체
                            1 -> items.filter { it.categoryInt == 1 } // 물감
                            2 -> items.filter { it.categoryInt == 2 } // 머리띠
                            3 -> items.filter { it.categoryInt == 3 } // 풍선
                            4 -> items.filter { it.categoryInt == 4 } // 오라
                            else -> emptyList()
                        }

                        items(currentItems) { item ->
                            ItemCard(item) { clickedItem ->
                                items = items.map {
                                    // 클릭한 아이템의 상태를 토글하고 같은 카테고리 아이템은 모두 해제
                                    if (it.id == clickedItem.id) {
                                        it.copy(isWorn = !it.isWorn)
                                    } else if (it.categoryInt == clickedItem.categoryInt) {
                                        it.copy(isWorn = false)
                                    } else {
                                        it
                                    }
                                }
                            }
                        }

                    }

                    // 획득 아이템 표시 텍스트
                    val filteredItems = items.filter { item ->
                        when (selectedTab) {
                            0 -> true // 전체 아이템
                            1 -> item.categoryInt == 1 // 물감
                            2 -> item.categoryInt == 2 // 머리띠
                            3 -> item.categoryInt == 3 // 풍선
                            4 -> item.categoryInt == 4 // 오라
                            else -> false
                        }
                    }

                    Text(
                        text = "획득 아이템 : 총 ${filteredItems.size}개",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(vertical = 15.dp, horizontal = 30.dp),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

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

@Composable
fun ItemCard(item: WearableItem, onClick: (WearableItem) -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp) // 정사각형 크기 설정
            .background(color = Color(0x80FFB9C7), shape = RoundedCornerShape(size = 18.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // 아이템 이미지 (임시 텍스트로 대체)
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = "아이템",
                    modifier = Modifier.size(50.dp)
                )
            }

            // 착용/해제 버튼
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(30.dp)
                    .clickable { onClick(item) }
            ) {
                Image(
                    painter = painterResource(
                        id = if (item.isWorn) R.drawable.closet_btn_dresson
                        else R.drawable.closet_btn_dressoff
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize() // 이미지가 버튼 영역을 채우도록 설정
                )
                Text(
                    text = if (item.isWorn) "해제" else "착용",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// 스트로크 글씨 함수
@Composable
fun StrokedText(
    text: String,
    fontSize: Int,
    strokeWidth: Float = 10f,
    color: Color = Color.White,
    strokeColor: Color = Color.Black
) {
    Box {
        // Stroke 텍스트
        Text(
            text = text,
            color = strokeColor,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = fontSize.sp,
                drawStyle = Stroke(
                    width = strokeWidth,
                    join = StrokeJoin.Round
                )
            )
        )
        // 일반 텍스트
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                color = color,
                fontSize = fontSize.sp
            )
        )
    }
}


// 아이템 장착 화면 - 상단 캐릭터 영역
@Composable
fun CharacterWithItems(wornItems: List<WearableItem>) {

    Box(modifier = Modifier.size(200.dp)) {
        // 특정 카테고리(예: "오라")의 아이템 배치 (캐릭터 뒤)
        wornItems.filter { it.category == "오라" }.forEach { item ->
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = "착용 아이템",
                modifier = Modifier
                    .size(item.size.dp)
                    .offset(item.position.x.dp, item.position.y.dp)
            )
        }

        // 캐릭터 이미지
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.all_img_whitecat),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .offset(x = 15.dp, y = 10.dp) // 이미지 위치 조정
            )
        }

        // 나머지 카테고리의 아이템 배치 (캐릭터 위)
        wornItems.filter { it.category != "오라" }.forEach { item ->
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = "착용 아이템",
                modifier = Modifier
                    .size(item.size.dp)
                    .offset(item.position.x.dp, item.position.y.dp)
            )
        }
    }
}