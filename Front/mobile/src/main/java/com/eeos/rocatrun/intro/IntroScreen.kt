package com.eeos.rocatrun.intro

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.home.HomeActivity
import kotlinx.coroutines.launch

@Composable
fun IntroScreen(
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        
        // skip 버튼
        Box(
            modifier = Modifier
                .padding(40.dp)
        ) {
            Text(
                text = "Skip",
                fontSize = 24.sp,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                    }
            )
        }

        // 인디케이터
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = if (pagerState.currentPage == index) Color.White
                            else Color.White.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
            }
        }

        // 이미지 페이저를 상단 여백을 두고 배치
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)  // 상단에 여백 추가
        ) {
        
            // 이미지 페이저
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = when (page) {
                                0 -> R.drawable.intro_img_coachmark1
                                1 -> R.drawable.intro_img_coachmark2
                                else -> R.drawable.intro_img_coachmark3
                            }
                        ),
                        contentDescription = "Intro image ${page + 1}",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (pagerState.currentPage > 0) {
                Image(
                    painter = painterResource(id = R.drawable.intro_icon_previous),
                    contentDescription = "Next",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(10.dp)
                        .size(30.dp)
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    )

            }

            // 다음 페이지 버튼 (1,2 페이지일 때만)
            if (pagerState.currentPage < 2) {
                Image(
                    painter = painterResource(id = R.drawable.intro_icon_next),
                    contentDescription = "Next",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(10.dp)
                        .size(30.dp)
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                )

            } else {
                // 시작하기 버튼 (마지막 페이지)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp)
                        .clickable {
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            context.startActivity(intent)
                            onClose()
                        }
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "시작하기",
                            style = TextStyle(
                                color = Color.Yellow,
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(R.font.neodgm))
                            )
                        )
                        Text(
                            text = ">",
                            style = TextStyle(
                                color = Color.Yellow,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.neodgm))
                            )
                        )
                    }
                }
            }
        }

    }
}