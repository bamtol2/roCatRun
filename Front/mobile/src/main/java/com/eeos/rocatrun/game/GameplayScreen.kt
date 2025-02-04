package com.eeos.rocatrun.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eeos.rocatrun.R
import com.eeos.rocatrun.result.MultiLoseScreen
import com.eeos.rocatrun.result.MultiWinScreen
import com.eeos.rocatrun.result.SingleLoseScreen
import com.eeos.rocatrun.result.SingleWinScreen


@Composable
fun GameplayScreen() {

    // 임시 테스트용 버튼
    var showMultiWinDialog by remember { mutableStateOf(false) }
    var showMultiLoseDialog by remember { mutableStateOf(false) }
    var showSingleWinDialog by remember { mutableStateOf(false) }
    var showSingleLoseDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.game_bg_gameplay),
            contentDescription = "game play page background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 200.dp),    // 원하는 만큼 상단 패딩 조절
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {

            GifImage(modifier = Modifier.size(100.dp),
                gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.game_gif_greencan}"
            )

            Text(
                text = "게임 중입니다..",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontSize = 45.sp,
                ),
                textAlign = TextAlign.Center,
            )

            Text(
                text = "워치 화면을 확인해 주세요",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontSize = 20.sp,
                ),
                textAlign = TextAlign.Center
            )

            // 임시 테스트용 버튼
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "멀티 승리",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.clickable { showMultiWinDialog = true }
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "멀티 패배",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.clickable { showMultiLoseDialog = true }
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "싱글 승리",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.clickable { showSingleWinDialog = true }
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "싱글 패배",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.clickable { showSingleLoseDialog = true }
                        )
                    }
                }
            }
        }

        // 모달 표시
        if (showMultiWinDialog) {
            MultiWinScreen()
        }
        else if (showMultiLoseDialog) {
            MultiLoseScreen()
        }
        else if (showSingleWinDialog) {
            SingleWinScreen()
        }
        else if (showSingleLoseDialog) {
            SingleLoseScreen()
        }
    }
}

// gif 불러오는 함수
@Composable
fun GifImage(modifier: Modifier = Modifier, gifUrl: String) {

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(gifUrl)
            .crossfade(false)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = "GIF Image",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}