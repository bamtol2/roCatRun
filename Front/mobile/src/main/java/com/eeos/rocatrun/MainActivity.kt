package com.eeos.rocatrun


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eeos.rocatrun.ui.theme.RoCatRunTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoCatRunTheme {
                // 로그인 여부 확인
                // 로그인이 안되어 있다면 LoginActivity로 이동

                // 로그인이 되어 있다면 HomeActivity로 이동
            }
        }
    }
}
