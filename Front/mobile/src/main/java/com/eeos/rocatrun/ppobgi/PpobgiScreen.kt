package com.eeos.rocatrun.ppobgi

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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.eeos.rocatrun.R
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.ppobgi.api.PpobgiViewModel
import kotlinx.coroutines.delay
import com.eeos.rocatrun.ui.components.GifImage

@Composable
fun PpobgiDialog(onDismiss: () -> Unit) {
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

            if (!isDrawing && !showResult) {

                // 초기 뽑기 버튼 화면
                Box (
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .fillMaxWidth()
                ){

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
                    ){
                        // 내용을 담을 Column
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // 안내 텍스트
                            Text(
                                text = "어떤 아이템이 나올까냥?\n기대된다냥~(⁎˃ᆺ˂)",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color(0xFF100810),
                                    fontSize = 25.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(vertical = 50.dp)
                            )
                            // 뽑기 버튼
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        if (token != null) {
                                            viewModel.drawItem(token, 1)
                                        } else {
                                            Log.d("pppobgi", "뽑기 토큰 에러")
                                        }
                                        isDrawing = true
                                    }
                                    .padding(top = 5.dp)
                            ) {
                                // 뽑기 버튼 배경
                                Image(
                                    painter = painterResource(id = R.drawable.ppobgi_btn_random),
                                    contentDescription = "뽑기 버튼",
                                    modifier = Modifier
                                        .width(350.dp)  // 버튼 가로 크기 증가
                                        .height(250.dp)  // 버튼 세로 크기 증가
                                )

                                // 코인 정보를 보여주는 Row
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.home_img_cancoin),
                                        contentDescription = "코인 아이콘",
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "100",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = Color(0xFF100810),
                                            fontSize = 36.sp,
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // 결과 처리
                    LaunchedEffect(drawResult) {
                        drawResult?.let {
                            // 뽑기 결과 표시 로직
                            showResult = true
                        }
                    }

                    // 에러 처리
                    LaunchedEffect(error) {
                        error?.let {
                            // 에러 메시지 표시 로직
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Rainbow gif를 모달창 위에 배치
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-150).dp)  // 모달창 상단으로 위치 조정
                            .zIndex(2f),  // 모달창보다 위에 표시
                        contentAlignment = Alignment.TopCenter
                    ) {
                        GifImage(
                            modifier = Modifier
                                .width(300.dp)
                                .height(200.dp),
                            gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.ppobgi_gif_rainbow}"
                        )
                    }

                }
            } else if (isDrawing) {
                // 뽑기 애니메이션 (Coil로 GIF 로드)
                Box(
                    modifier = Modifier
                        .size(400.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GifImage(
                        modifier = Modifier.fillMaxSize(),
                        gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.ppobgi_gif_giftbox}")
                }

                // 애니메이션 완료 후 결과 표시로 전환
                LaunchedEffect(isDrawing) {
                    delay(7000) // 애니메이션 시간
                    isDrawing = false
                    showResult = true
                }
            } else if (showResult) {
                // 뽑기 결과 표시
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 뽑은 아이템 이미지
                    Image(
                        painter = painterResource(id = R.drawable.game_img_monster1),
                        contentDescription = "뽑은 아이템",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(16.dp)
                    )

                    Text(
                        text = "축하합니다!",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("확인")
                    }
                }
            }

        }
    }
}

// 배경 어둡게 처리를 위한 Scrim
@Composable
fun DialogScrim(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        content()
    }
}