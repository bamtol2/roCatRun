package com.eeos.rocatrun.presentation

import android.util.Log
import android.os.Bundle
import android.os.Build
import androidx.annotation.RequiresApi
import android.net.Uri
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.eeos.rocatrun.R
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import android.content.Context
import android.widget.Toast

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen2()
        }
    }

    // 모바일 앱 실행 요청 함수(모바일 게임 종료 화면으로 이동하게)
    fun goMobileApp() {
        val messageClient: MessageClient = Wearable.getMessageClient(this)
        val path = "/start_mobile_app"
        val messageData = "Start Game".toByteArray()

        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            if (nodes.isNotEmpty()) {
                val nodeId = nodes.first().id
                Log.d("WearApp", "연결된 노드: ${nodes.first().displayName}")

                messageClient.sendMessage(nodeId, path, messageData).apply {
                    addOnSuccessListener {
                        Toast.makeText(this@ResultActivity, "모바일 앱 시작 요청 전송 완료", Toast.LENGTH_SHORT).show()
                    }
                    addOnFailureListener {
                        Toast.makeText(this@ResultActivity, "모바일 앱 전송 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.d("WearApp", "연결된 노드가 없습니다.")
                Toast.makeText(this, "연결된 디바이스가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
@Composable
fun SplashScreen2() {
    val context = LocalContext.current
    val resultActivity = context as ResultActivity  // MainActivity의 메서드 호출을 위해 캐스팅

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "로캣냥",
            style = TextStyle(
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.neodgm))
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "폰으로 이동해서\n결과를 확인하세요",
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.neodgm)),
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(32.dp))

        // 확인 버튼
        Button(
            onClick = {
                resultActivity.goMobileApp()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FFCC)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(50.dp)
        ) {
            Text(
                text = "확인",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.neodgm))
                )
            )
        }
    }
}

