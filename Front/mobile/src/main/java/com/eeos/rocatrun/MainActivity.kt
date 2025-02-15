package com.eeos.rocatrun

import android.os.Bundle
import android.util.Log
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.login.LoginActivity
import com.eeos.rocatrun.login.data.RetrofitClient
import com.eeos.rocatrun.login.data.TokenManager
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val accessToken = TokenStorage.getAccessToken(this)

        // 1) 먼저 토큰이 유효한지 검사
        if (TokenManager.isTokenVaild(accessToken)) {
            Log.i("MainActivity", "토큰 유효: $accessToken")

            // 2) 토큰이 유효하면, 회원(캐릭터) 정보가 있는지 checkMember API로 확인
            checkMemberAndNavigate(accessToken)

        } else {
            // 토큰이 없거나 만료된 경우 → 토큰 갱신 시도
            Log.i("MainActivity", "토큰 만료 또는 없음. 토큰 갱신 시도")
            TokenManager.refreshTokensIfNeeded(this) { isRefreshSuccessful ->
                if (isRefreshSuccessful) {
                    val newAccessToken = TokenStorage.getAccessToken(this)
                    if (TokenManager.isTokenVaild(newAccessToken)) {
                        // 갱신된 토큰이 유효하면 다시 회원 여부 확인
                        Log.i("MainActivity", "토큰 갱신 성공, 다시 checkMember")
                        checkMemberAndNavigate(newAccessToken)
                    } else {
                        Log.e("MainActivity", "갱신된 토큰도 유효하지 않음")
                        navigateToLogin()
                    }
                } else {
                    // 리프레시 실패 시 → 로그인 필요
                    Log.i("MainActivity", "토큰 갱신 실패. 로그인 필요")
                    navigateToLogin()
                }
            }
        }
    }

    /**
     * checkMember API를 호출해, 회원(캐릭터)이 이미 등록되어 있는지 확인하고,
     * 결과에 따라 Home 또는 Login으로 분기하는 함수
     */
    private fun checkMemberAndNavigate(accessToken: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.checkMember("Bearer $accessToken")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        // 이미 회원(캐릭터 정보 존재)
                        Log.i("MainActivity", "이미 캐릭터 정보 존재 → Home 이동")
                        navigateToHome()
                    } else {
                        // 회원(캐릭터) 정보가 없음 → LoginActivity로 이동해서 회원가입 유도
                        Log.i("MainActivity", "캐릭터 정보 없음 → LoginActivity 이동")
                        navigateToLogin()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "checkMember API 호출 중 오류: ${e.message}")
                // 에러 발생 시, 로그인화면으로 보내거나 처리
                navigateToLogin()
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
            putExtra("message", "회원가입(캐릭터 생성)이 필요합니다.")
        }
        startActivity(intent)
        finish()
    }
}
