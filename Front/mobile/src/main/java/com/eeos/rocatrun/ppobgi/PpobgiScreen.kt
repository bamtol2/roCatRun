package com.eeos.rocatrun.ppobgi

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.eeos.rocatrun.R
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.ppobgi.api.PpobgiViewModel
import kotlinx.coroutines.delay
import com.eeos.rocatrun.ui.components.GifImage
import com.eeos.rocatrun.ui.components.StrokedText

@Composable
fun PpobgiDialog(
    onDismiss: () -> Unit,
    refreshHomeData: () -> Unit
 ) {
    val viewModel: PpobgiViewModel = viewModel()
    val drawResult by viewModel.drawResult.collectAsState()
    val error by viewModel.error.collectAsState()
    val remainingCoins by viewModel.remainingCoins.collectAsState()

    var isDrawing by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val token = TokenStorage.getAccessToken(context)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {

            // 상태 전환을 위한 LaunchedEffect
            LaunchedEffect(drawResult) {
                if (drawResult != null && isDrawing) {
                    delay(6000) // 애니메이션 시간
                    isDrawing = false
                    showResult = true
                }
            }

            when {
                isDrawing -> {
                    // 상자 애니메이션
                    Box(
                        modifier = Modifier
                            .size(500.dp)
                            .padding(16.dp)
                            .clickable {
                                isDrawing = false
                                showResult = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        GifImage(
                            modifier = Modifier.fillMaxSize(),
                            gifUrl = "android.resource://com.eeos.rocatrun/${getRarityGifUrl(drawResult?.rarity)}"
                        )
                    }
                }
                showResult -> {

                    // 뽑기 결과 표시
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(24.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            drawResult?.let { item ->

                                // 로컬에서 아이템 이미지 찾기
                                val resourceName = "${item.name}_off"
                                val resourceId = context.resources.getIdentifier(
                                    resourceName,
                                    "drawable",
                                    context.packageName
                                )
                                Log.d("뽑기", "리소스 찾음 - resourceId: $resourceId")

                                // 아이템 이미지
                                Image(
                                    painter = painterResource(id = resourceId),
                                    contentDescription = "뽑은 아이템",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(16.dp),
                                    contentScale = ContentScale.Fit
                                )

                                // 아이템 이름
                                Text(
                                    text = item.koreanName,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 30.sp  // sp 단위 사용
                                    ),
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = Color.White
                                )

                                // 등급
                                val (textColor, strokeColor) = getRarityColors(item.rarity)
                                StrokedText(
                                    text = "${item.rarity}",
                                    fontSize = 20,
                                    modifier = Modifier.padding(top = 4.dp),
                                    color = textColor,
                                    strokeColor = strokeColor
                                )

                                Spacer(modifier = Modifier.height(15.dp))

                                // 설명
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 15.sp  // sp 단위 사용
                                    ),
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

//                                remainingCoins?.let { coins ->
//                                    Text(
//                                        text = "잔여 코인: $coins",
//                                        style = MaterialTheme.typography.titleLarge.copy(
//                                            fontSize = 15.sp  // sp 단위 사용
//                                        ),
//                                        modifier = Modifier.padding(top = 4.dp)
//                                    )
//                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(1.dp)  // 버튼 사이 간격
                                ) {
                                    CustomButton(
                                        text = "한번 더!",
                                        onClick = {
                                            viewModel.clearDrawResult()
                                            isDrawing = false
                                            showResult = false
                                        },
                                        buttonHeight = 80,
                                        modifier = Modifier.weight(1f)
                                    )

                                    CustomButton(
                                        text = "확인",
                                        onClick = {
                                            viewModel.clearDrawResult()
                                            isDrawing = false
                                            showResult = false
                                            refreshHomeData()
                                            onDismiss()
                                        },
                                        buttonHeight = 80,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    // 초기 뽑기 버튼 화면
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onDismiss() }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 40.dp)
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        ) {

                            Box(
                                modifier = Modifier
                                    .width(324.dp)
                                    .height(250.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(color = Color(0xFFF6C0FA))
                                    .border(
                                        width = 3.dp,
                                        color = Color(0xFF9A9FE4),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .zIndex(1f)  // zIndex 설정
                            ) {
                                // 닫기 아이콘
                                Image(
                                    painter = painterResource(id = R.drawable.game_icon_close),
                                    contentDescription = "Close",
                                    modifier = Modifier
                                        .align(Alignment.TopStart)  // 왼쪽 중앙 정렬
                                        .padding(start = 16.dp, top = 16.dp)  // 패딩 조정
                                        .size(24.dp)
                                        .clickable { onDismiss() }
                                )
                                // 내용을 담을 Column
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 60.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {

                                    StrokedText(
                                        text = "어떤 아이템이 나올까냥?",
                                        fontSize = 23,
                                        color = Color(0xFF100810),
                                        strokeColor = Color(0xFFD599C5),
                                        modifier = Modifier.padding(vertical = 10.dp),
                                    )
                                    StrokedText(
                                        text = "뽑아보라냥~(⁎˃ᆺ˂)",
                                        fontSize = 23,
                                        color = Color(0xFF100810),
                                        strokeColor = Color(0xFFD599C5),
                                        modifier = Modifier.padding(vertical = 10.dp),
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        // 뽑기 버튼
                                        CustomButton(
                                            text = "뽑기",
                                            onClick = {
                                                if (token != null) {
                                                    Log.d("뽑기", "뽑기 시작 - 토큰: ${token.take(10)}...")
                                                    viewModel.drawItem(token, 1)
                                                    isDrawing = true
                                                } else {
                                                    Log.d("뽑기", "토큰 에러")
                                                }
                                            },
                                            showCoin = true,
                                            coinAmount = "x 100",
                                            buttonHeight = 150,
                                            modifier = Modifier.weight(0.5f)
                                        )
                                    }
                                }
                            }

                            // 에러 처리
                            LaunchedEffect(error) {
                                error?.let {
                                    // 코인 수 모자라면 모달 띄워주어야 함
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            }

                            // Rainbow gif
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = (-120).dp)
                                    .zIndex(2f),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                GifImage(
                                    modifier = Modifier
                                        .width(250.dp)
                                        .height(180.dp),
                                    gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.ppobgi_gif_rainbow}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getRarityColors(rarity: String): Pair<Color, Color> {
    return when (rarity) {
        "NORMAL" -> Color.White to Color(0xFFA3A1A5)
        "RARE" -> Color.White to Color(0xFF018F2C)
        "UNIQUE" -> Color.White to Color(0xFF1646CB)
        "EPIC" -> Color.White to Color(0xFF6C13E1)
        "LEGENDARY" -> Color(0xFFFF0080) to Color(0xFFFFFF00)
        else -> Color.White to Color.White
    }
}

fun getRarityGifUrl(rarity: String?): Int {
    return when (rarity) {
        "NORMAL" -> R.drawable.ppobgi_gif_box_r
        "RARE" -> R.drawable.ppobgi_gif_box_g
        "UNIQUE" -> R.drawable.ppobgi_gif_box_b
        "EPIC" -> R.drawable.ppobgi_gif_box_p
        "LEGENDARY" -> R.drawable.ppobgi_gif_box_y
        else -> R.drawable.ppobgi_gif_box_r  // 기본값
    }
}