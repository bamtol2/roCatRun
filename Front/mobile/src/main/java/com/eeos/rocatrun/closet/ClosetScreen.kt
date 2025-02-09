package com.eeos.rocatrun.closet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.game.InfoScreen
import com.eeos.rocatrun.game.MainButtons
import com.eeos.rocatrun.game.TopNavigation

@Composable
fun ClosetScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("전체", "물감", "머리띠", "풍선")
    val items = mapOf(
        0 to listOf("item1", "item2", "item3", "item4"),
        1 to listOf("paint1", "paint2"),
        2 to listOf("headband1", "headband2"),
        3 to listOf("balloon1", "balloon2")
    )
    var equippedItem by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.game_bg_gameroom),
            contentDescription = "closet select background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 캐릭터 영역
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.all_img_whitecat),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }

            // 탭과 아이템 목록 영역
            Column(modifier = Modifier.weight(1f)) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 8.dp,
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                // 아이템 목록 (LazyVerticalGrid)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currentItems = items[selectedTab] ?: emptyList()
                    items(currentItems) { item ->
                        ItemCard(item, equippedItem) { clickedItem ->
                            equippedItem =
                                if (equippedItem == clickedItem) null else clickedItem // 착용/해제 토글
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(item: String, equippedItem: String?, onClick: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            // 아이템 이미지 (임시 텍스트로 대체)
            Text(text = item)
        }
        Button(onClick = { onClick(item) }) {
            Text(text = if (equippedItem == item) "해제" else "착용")
        }
    }
}