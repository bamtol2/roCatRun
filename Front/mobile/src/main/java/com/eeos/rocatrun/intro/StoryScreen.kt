package com.eeos.rocatrun.intro

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.ui.components.GifImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun StoryScreen(
    onFollowClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.all_bg_intro),
            contentDescription = "game play page background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 텍스트 영역을 Box로 감싸서 고정 높이 지정
                Box(
                    modifier = Modifier
                        .padding(top = 80.dp)
                        .height(200.dp),  // 텍스트 영역 높이 고정
                    contentAlignment = Alignment.TopStart
                ) {

                    var scrollState = rememberScrollState()
                    var textHeight by remember { mutableStateOf(0) }
                    val scope = rememberCoroutineScope()

                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .onSizeChanged { size ->
                                textHeight = size.height
                            }
                    ) {

                        TypewriterText(
                            text = "2025년, 고양이들의 치명적인 귀여움으로 지구가 멸망했다냥…!\n\n" +
                                    "터전을 잃은 고양이들은 새로운 왕국을 세우기 위해 화성으로 떠나기로 결심했다!\n\n" +
                                    "이름하여… 로캣냥 프로젝트!\n\n" +
                                    "하지만 화성까지 가는 길엔 강력한 보스들이 기다리고 있다냥!\n\n" +
                                    "힘껏 달리고, 점프하고, 보스를 무찌르며 화성으로 향하자냥!!\n\n" +
                                    "자, 준비됐냥?! 지금부터 내가 설명해 주겠다냥! ",
                            onTextUpdate = { currentText ->
                                // 텍스트가 업데이트될 때마다 스크롤 위치 조정
                                if (textHeight > 200.dp.value) {  // Box 높이보다 텍스트가 길 경우
                                    scope.launch {
                                        scrollState.animateScrollTo(textHeight)
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(95.dp))

                // 보스 이미지들
                Box(
                    modifier = Modifier
                        .height(150.dp)  // 보스 이미지 영역 높이 고정
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // 각 보스 이미지를 FloatingImage로 감싸기
                    // boss1을 위에 배치
                    FloatingImage(
                        imageRes = R.drawable.all_img_boss1,
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = (-30).dp)  // 위로 올림
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),  // boss2, boss3는 아래에 배치
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FloatingImage(
                            imageRes = R.drawable.all_img_boss2,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.width(50.dp))  // boss1 자리 비워두기
                        FloatingImage(
                            imageRes = R.drawable.all_img_boss3,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                GifImage(
                    modifier = Modifier.size(100.dp),
                    gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.all_img_catback}"
                )

                Spacer(modifier = Modifier.height(50.dp))

                Image(
                    painter = painterResource(R.drawable.intro_btn_follow),
                    contentDescription = "follow button",
                    modifier = Modifier.clickable {
                        onFollowClick()  // IntroScreen으로 이동
                    }
                )
            }
        }
    }
}

@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    delay: Long = 100L, // 각 글자가 나타나는 딜레이
    onTextUpdate: (String) -> Unit
) {
    var displayText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(text) {
        while (currentIndex < text.length) {
            delay(delay)
            displayText = text.substring(0, currentIndex + 1)
            currentIndex++
            onTextUpdate(displayText)
        }
    }

    Text(
        text = displayText,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge.copy(
            color = Color.White,
            fontSize = 17.sp
        ),
        lineHeight = 25.sp
    )
}

@Composable
fun FloatingImage(
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    // 각 이미지마다 다른 시작 위치를 가지도록 랜덤값 사용
    val randomOffset = remember { (Math.random() * PI).toFloat() }
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = modifier
            .offset(y = (10 * sin(offset * 2 * PI + randomOffset)).dp) // 각각 다른 위치에서 시작
    )
}