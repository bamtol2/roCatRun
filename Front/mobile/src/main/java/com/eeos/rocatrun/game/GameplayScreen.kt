package com.eeos.rocatrun.game

import android.content.Intent
import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun GameplayScreen(onShareClick: () -> Unit) {

    val gpxFileReceived by GamePlayService.gpxFileReceived.observeAsState(false)
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

            Spacer(modifier = Modifier.height(150.dp))

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
                        context.startActivity(intent)
                    }
                )
            }

        }

        if (gpxFileReceived) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Green)
                    .padding(8.dp)
            ) {
                Text("GPX 파일 수신 완료", color = Color.White)
            }

            Button(
                onClick = onShareClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text("GPX 파일 공유")
            }
        }

        // 모달 표시
        when (val currentModalState = modalState) {
            is GamePlayService.ModalState.LevelUp -> {
                LevelUpScreen(
                    oldLevel = currentModalState.oldLevel,
                    newLevel = currentModalState.newLevel,
                    onDismiss = {
                        // 레벨업 모달을 닫고 대기 중인 게임 결과 모달 표시
                        GamePlayService.pendingGameResult?.let { result ->
                            GamePlayService._modalState.postValue(result)
                            GamePlayService.pendingGameResult = null
                        } ?: GamePlayService.resetModalState()
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
