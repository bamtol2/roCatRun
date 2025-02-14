package com.eeos.rocatrun.closet


import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eeos.rocatrun.R
import com.eeos.rocatrun.closet.api.ClosetViewModel
import com.eeos.rocatrun.home.HomeActivity
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeApi::class)
@Composable
fun SaveCheckScreen(
    message: String,
    onDismissRequest: () -> Unit,
    captureController: CaptureController,
    closetViewModel: ClosetViewModel,
    token: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,  // 박스를 중앙에 정렬
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            // 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.closet_bg_save_check),
                contentDescription = "Modal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // 메시지 및 확인 텍스트 표시
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .offset(y = 30.dp)
                )

                Row() {
                    // 터치 가능한 텍스트
                    Text(
                        text = "나가기",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 16.dp)
                            .offset(y = 43.dp)
                            .clickable {
                                context.startActivity(Intent(context, HomeActivity::class.java))
                            }
                    )

                    Text(
                        text = "저장하기",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 16.dp)
                            .offset(y = 43.dp)
                            .clickable {
                                scope.launch {
                                    try {
                                        val bitmap = captureController.captureAsync().await()
                                        saveImageToDownloads(context, bitmap, closetViewModel, token)
                                        closetViewModel.changeEquipStatus(auth = token, inventoryIds = closetViewModel.equippedItems.value)
//                                        onDismissRequest()
                                        delay(500)
                                        context.startActivity(Intent(context, HomeActivity::class.java))
                                    } catch (error: Throwable) {
                                        Toast.makeText(
                                            context,
                                            "이미지 저장 실패: ${error.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                    )

                }

            }
        }
    }

}