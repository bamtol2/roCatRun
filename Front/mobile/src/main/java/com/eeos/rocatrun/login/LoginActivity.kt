package com.eeos.rocatrun.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.eeos.rocatrun.login.LoginScreen
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import android.content.Intent
import com.eeos.rocatrun.login.social.LoginResponse

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인텐트로부터 loginResponse 추출
        val loginResponse = intent.getSerializableExtra("login_response") as? LoginResponse

        enableEdgeToEdge()
        setContent {
            RoCatRunTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 추출한 loginResponse를 LoginScreen에 전달
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        loginResponse = loginResponse
                    )
                }
            }
        }
    }
}