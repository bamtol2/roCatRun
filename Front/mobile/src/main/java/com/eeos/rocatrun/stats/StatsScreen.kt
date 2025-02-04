package com.eeos.rocatrun.stats

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.eeos.rocatrun.R
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.ui.theme.MyFontFamily

@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Composable
fun StatsScreen() {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("일", "주", "월")
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
        initialPageOffsetFraction = 0f,
        initialPage = 0,
    )
    val tabIndex = pagerState.currentPage

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.stats_bg_image),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .offset(x = 0.dp, y = 47.dp),
        ) {
            // 홈 버튼
            Button(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 10.dp, y = 0.dp),
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

            // 탭
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .offset(x = 0.dp, y = 50.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                TabRow(
                    selectedTabIndex = tabIndex,
                    containerColor = Color.Transparent,
                    modifier = Modifier.fillMaxWidth(),
                    indicator = { /* indicator 없애기 */ },
                    divider = { /* 탭 아래 구분선 없애기 */ }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    color = if (tabIndex == index) Color(0xFF36DBEB) else Color(0xFFFFFFFF),
                                    fontSize = 42.86.sp,
                                    fontFamily = MyFontFamily,
                                )
                            }
                        )
                    }
                }
            }

            // 탭 별 화면
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.77f)
                    .padding(10.dp)
                    .offset(x = 0.dp, y = 130.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
                    when (page) {
                        0 -> DayStatsScreen()
                        1 -> WeekStatsScreen()
                        2 -> MonStatsScreen()
                    }
                }
            }
        }

    }
}