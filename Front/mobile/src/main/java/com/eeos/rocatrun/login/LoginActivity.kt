package com.eeos.rocatrun.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import android.content.Intent
import com.eeos.rocatrun.login.data.LoginResponse
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 네비게이션 바 색상 설정
        window.navigationBarColor = Color(0xFF051330).toArgb()
        window.statusBarColor = Color(0xFF051330).toArgb()
        // 인텐트로부터 loginResponse 추출
        val loginResponse = intent.getParcelableExtra<LoginResponse>("login_response")
        Log.i("로그인", "onCreate에서 로그인 리스폰스 받음 $loginResponse")
        setContent {
            RoCatRunTheme {
                    // 추출한 loginResponse를 LoginScreen에 전달
                    LoginScreen(
                        loginResponse = loginResponse
                    )

            }
        }
    }

    // 새로운 인텐트가 전달되었을 때 호출되는 메서드
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val loginResponse = intent?.getParcelableExtra<LoginResponse>("login_response")
        Log.i("로그인", "onNewIntent에서 로그인 리스폰스 받음 $loginResponse")

        // 필요한 경우 새로 받은 loginResponse를 화면에 다시 적용하는 로직 추가
        setContent {
            RoCatRunTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        loginResponse = loginResponse
                    )
                }
            }
        }
    }
}
