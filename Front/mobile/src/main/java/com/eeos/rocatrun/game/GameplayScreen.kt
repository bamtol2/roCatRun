package com.eeos.rocatrun.game

import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.result.LevelUpScreen
import com.eeos.rocatrun.result.MultiLoseScreen
import com.eeos.rocatrun.result.MultiWinScreen
import com.eeos.rocatrun.result.SingleLoseScreen
import com.eeos.rocatrun.result.SingleWinScreen
import com.eeos.rocatrun.service.GamePlayService
import com.eeos.rocatrun.ui.components.GifImage


@Composable
fun GameplayScreen(firstBossHealth: Int) {

    val myResult by GamePlayService.myResult.observeAsState()
    val playerResults by GamePlayService.playerResults.observeAsState(emptyList())
    val myRank by GamePlayService.myRank.observeAsState(0)
    val modalState by GamePlayService.modalState.observeAsState(GamePlayService.ModalState.None)
    val context = LocalContext.current

    LaunchedEffect(modalState) {
        Log.d("Modal", "Modal state changed: $modalState")
    }

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

        // 보스 이미지 (텍스트보다 위에 배치)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),  // 상단 여백 조정
            contentAlignment = Alignment.Center
        ) {
            FloatingBoss(firstBossHealth)
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 300.dp),    // 원하는 만큼 상단 패딩 조절
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {

//            GifImage(modifier = Modifier.size(100.dp),
//                gifUrl = "android.resource://com.eeos.rocatrun/${R.drawable.game_gif_greencan}"
//            )

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

            Spacer(modifier = Modifier.height(80.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "홈으로 이동",
                    color = Color.White,
                    fontSize = 30.sp,
                    modifier = Modifier.clickable {
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                    }
                )
            }
        }

        // 모달 표시
        when (val currentModalState = modalState) {
            is GamePlayService.ModalState.LevelUp -> {
                LevelUpScreen(
                    oldLevel = currentModalState.oldLevel,
                    newLevel = currentModalState.newLevel,
                    onDismiss = {
                        if (GamePlayService.pendingGameResult != null) {
// 대기 중인 게임 결과 모달이 있으면 해당 모달 상태를 적용
                            GamePlayService._modalState.postValue(GamePlayService.pendingGameResult)
                            GamePlayService.pendingGameResult = null
                        } else {
// 대기 중인 결과 모달이 없다면 모달 상태를 초기화 (모달 닫기)
                            GamePlayService.resetModalState()
                        }
                    }
                )
            }
            is GamePlayService.ModalState.MultiWin ->
                MultiWinScreen(myResult = myResult, myRank = myRank, playerResults = playerResults)
            is GamePlayService.ModalState.MultiLose ->
                MultiLoseScreen(myResult = myResult, myRank = myRank, playerResults = playerResults)
            is GamePlayService.ModalState.SingleWin ->
                SingleWinScreen(myResult = myResult)
            is GamePlayService.ModalState.SingleLose ->
                SingleLoseScreen(myResult = myResult)
            else -> {} // ModalState.None
        }
    }
}

@Composable
fun FloatingBoss(firstBossHealth: Int) {
    var xOffset by remember { mutableStateOf(0f) }
    var yOffset by remember { mutableStateOf(0f) }

    // X축 애니메이션 (좌우 이동)
    val animatedX by animateFloatAsState(
        targetValue = xOffset,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Y축 애니메이션 (위아래 이동)
    val animatedY by animateFloatAsState(
        targetValue = yOffset,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // 애니메이션 시작
    LaunchedEffect(key1 = true) {
        xOffset = 40f  // 좌우 이동 범위 감소
        yOffset = 20f  // 위아래 이동 범위 감소
    }

    // 보스 이미지 리소스 선택
    val bossImageRes = when (firstBossHealth) {
        1000 -> R.drawable.all_img_boss3
        5000 -> R.drawable.all_img_boss2
        6000 -> R.drawable.all_img_boss1
        else -> R.drawable.all_img_boss3
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = bossImageRes),
            contentDescription = "Boss Image",
            modifier = Modifier
                .size(100.dp)
                .offset(
                    x = (animatedX - 20f).dp,  // 중앙을 기준으로 좌우로 움직이도록 offset 조정
                    y = (animatedY - 10f).dp   // 중앙을 기준으로 위아래로 움직이도록 offset 조정
                )
        )
    }
}