package com.eeos.rocatrun


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import android.content.Intent
import android.util.Log
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.login.LoginActivity
import com.eeos.rocatrun.login.data.Token
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.login.data.TokenManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val accessToken = TokenStorage.getAccessToken(this)

        if (TokenManager.isTokenVaild(accessToken)) {
            Log.i("토큰 유효함", "토큰 있음!")
            Log.i("MainActivity", "엑세스 토큰: $accessToken")
            Log.i("MainActivity", "리프레시 토큰: ${TokenStorage.getRefreshToken(this)}")
            navigateToHome()
        } else {
            Log.i("토큰 유효하지 않음", "토큰 유효 X, 갱신 시도")
            TokenManager.refreshTokensIfNeeded(this) { isRefreshSuccessful ->
                if (isRefreshSuccessful) {
                    val newAccessToken = TokenStorage.getAccessToken(this)
                    Log.i("새로운 토큰 확인", "$newAccessToken")
                    if (TokenManager.isTokenVaild(newAccessToken)) {
                        Log.i("토큰 갱신 성공", "새 토큰 유효")
                        navigateToHome()
                    } else {
                        Log.e("토큰 검증 실패", "갱신된 토큰이 유효하지 않음")
                        navigateToLogin()
                    }
                } else {
                    Log.i("토큰 갱신 실패", "로그인 필요")
                    Log.i("리프레시 토큰 확인", "${TokenStorage.getRefreshToken(this)}")
                    navigateToLogin()
                }
            }
        }
    }
    private fun navigateToHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("message", "세션이 만료되었습니다. 다시 로그인해주세요.")
        }
        startActivity(intent)
        finish()
    }



}
