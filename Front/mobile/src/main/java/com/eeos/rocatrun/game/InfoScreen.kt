package com.eeos.rocatrun.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eeos.rocatrun.R
import com.eeos.rocatrun.ui.components.StrokedText
import kotlinx.coroutines.launch

@Composable
fun InfoScreen(
    onDismissRequest: () -> Unit
){
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
            ),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(700.dp)
                .border(
                    width = 3.dp,
                    color = Color(0xFFCC00FF)
                )
                .background(color = Color(0xE50C010E))
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
                        .background(Color(0xFFCC00FF))
                        .height(50.dp)

                ) {
                    // 닫기 아이콘
                    Image(
                        painter = painterResource(id = R.drawable.game_icon_close),
                        contentDescription = "Close",
                        modifier = Modifier
                            .align(Alignment.CenterStart)  // 왼쪽 중앙 정렬
                            .padding(start = 10.dp)
                            .size(32.dp)
                            .clickable { onDismissRequest() }
                    )

                    // 보스 정보 텍스트
                    Text(
                        text = "게임 Rule",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.Black,
                            fontSize = 32.sp,
                        ),
                        modifier = Modifier.align(Alignment.Center),  // 박스 중앙 정렬
                        textAlign = TextAlign.Center
                    )
                }
                Column (
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    StrokedText(
                        text = "게임 시작 전 워치를 꼭 연결 하라냥!",
                        fontSize = 17,
                        strokeWidth = 15f,
                        color = Color.Black,
                        strokeColor = Color(0xFFEEABFF)
                    )

                    Box (
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                    ){
                        Text(
                            text = "아래 자세한 게임 설명을 읽고,\n보스들을 해치워서 화성까지 달려보자냥!",
                            color = Color.White,
                            fontSize = 15.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(440.dp),  // 전체 높이 증가
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    val pagerState = rememberPagerState(pageCount = { 5 })
                    val coroutineScope = rememberCoroutineScope()

                    //horizontalpager 넣고 그 안에 이미지 넣고, 좌우로 움직일 수 있는 버튼 제공 < >
                    // 이미지 슬라이더
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)  // 상단 Box가 가능한 많은 공간 차지
                    ) {
                        // 이미지 페이저
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            Image(
                                painter = painterResource(
                                    id = when (page) {
                                        0 -> R.drawable.game_img_gameinfo1
                                        1 -> R.drawable.game_img_gameinfo2
                                        2 -> R.drawable.game_img_gameinfo3
                                        3 -> R.drawable.game_img_gameinfo4
                                        else -> R.drawable.game_img_gameinfo5
                                    }
                                ),
                                contentDescription = "Info Image ${page + 1}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        // 좌우 이동 버튼
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // 왼쪽 영역 (첫 페이지에서도 공간 유지)
                            Box(modifier = Modifier
                                .size(50.dp)
                                .padding(start = 1.dp)
                            ) {
                                if (pagerState.currentPage > 0) {
                                    Text(
                                        text = "<",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        modifier = Modifier
                                            .clickable {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                                }
                                            }
                                            .align(Alignment.Center)
                                    )
                                }
                            }

                            // 오른쪽 영역
                            Box(modifier = Modifier
                                .padding(end = 1.dp)
                                .size(50.dp)
                            ) {
                                if (pagerState.currentPage < 5) {
                                    Text(
                                        text = ">",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        modifier = Modifier
                                            .clickable {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                                }
                                            }
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }

                    // 페이지 인디케이터
                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(5) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (pagerState.currentPage == index)
                                            Color(0xFFCC00FF)
                                        else
                                            Color.Gray,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}