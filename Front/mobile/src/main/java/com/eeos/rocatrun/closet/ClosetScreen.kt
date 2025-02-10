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

@Composable
fun ClosetScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("전체", "물감", "머리띠", "풍선", "오라")
    val items = mapOf(
        0 to listOf("item1", "item2", "item3", "item4"),
        1 to listOf("paint1", "paint2"),
        2 to listOf("headband1", "headband2"),
        3 to listOf("balloon1", "balloon2"),
        4 to listOf("ora1","ora2")
    )
    var equippedItem by remember { mutableStateOf<String?>(null) }

    // 상단 홈 아이콘 버튼

    val context = LocalContext.current

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
            Box(
                modifier = Modifier
                    .weight(1f)
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

            // 탭과 아이템 목록 영역
            Column(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 10.dp)

            ) {
                CustomTabRow(
                    tabs = tabs,
                    selectedTabIndex = selectedTab,
                    onTabSelected = { index -> selectedTab = index }
                )

                // 아이템 목록 (LazyVerticalGrid)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .border(width = 4.dp, color = Color(0xFFFFB9C7))
                        .background(color = Color(0xD6B9999F), shape = RoundedCornerShape(12.dp))
                        .padding(8.dp), // 내부 여백 추가,
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

                // 획득 아이템 표시 텍스트
                Text(
                    text = "획득 아이템 : 총 ${items[selectedTab]?.size ?: 0}개",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White,
                    fontSize = 14.sp
                )
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
fun ItemCard(item: String, equippedItem: String?, onClick: (String) -> Unit) {
    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp) // 정사각형 크기 설정
            .background(color = Color(0x80FFB9C7), shape = RoundedCornerShape(size = 18.dp))
    ){
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
                Text(text = item)
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
                        id = if (equippedItem == item) R.drawable.closet_btn_dresson
                        else R.drawable.closet_btn_dressoff
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize() // 이미지가 버튼 영역을 채우도록 설정
                )
                Text(
                    text = if (equippedItem == item) "해제" else "착용",
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
