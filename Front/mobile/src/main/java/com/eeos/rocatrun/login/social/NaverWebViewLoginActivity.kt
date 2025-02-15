package com.eeos.rocatrun.login.social

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.eeos.rocatrun.R
import com.eeos.rocatrun.login.LoginActivity
import com.eeos.rocatrun.login.data.LoginResponse
import com.eeos.rocatrun.login.data.RetrofitClient
import com.eeos.rocatrun.login.data.TokenStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NaverWebViewLoginActivity : Activity() {

    private lateinit var webView: WebView

    companion object {
        private const val NAVER_AUTH_URL = "https://nid.naver.com/oauth2.0/authorize"
        private const val CLIENT_ID = "a03_KcmGfXFxM8hvVoGL"
        private const val REDIRECT_URI = "https://i12e205.p.ssafy.io:8080/api/auth/callback/naver"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_login)
        Log.i("네이버", "네이버 시작")

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true

        // OAuth 로그인 페이지 로드
        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("nid.naver.com")
            .path("/oauth2.0/authorize")
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("state", "state")
            .build()

        webView.loadUrl(authUrl.toString())

        // 리다이렉트 URI 처리
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.startsWith(REDIRECT_URI)) {
                    handleRedirect(url)
                    return true
                }
                return false
            }
        }
    }

    private fun handleRedirect(url: String) {
        val uri = Uri.parse(url)
        val code = uri.getQueryParameter("code")
        val state = uri.getQueryParameter("state")

        if (code != null) {
            Log.i("NaverWebViewLoginActivity", "인가코드 수신: $code")
            Log.i("state", "state 수신: $state")
            sendAuthCodeToBackend(code,state)
        } else {
            Log.e("NaverWebViewLoginActivity", "인가 코드 없음")
            finish()
        }
    }

    private fun sendAuthCodeToBackend(code: String?,state: String?) {
        if (code.isNullOrEmpty() || state.isNullOrEmpty()) {
            Log.e("NaverWebViewLoginActivity", "code 또는 state 값이 null")
            finish()
            return
        }
        val apiService = RetrofitClient.apiService
        apiService.naverCallback(code,state).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {

                    val loginResponse = response.body()
                    Log.i("naver", "로그인 성공,response: $loginResponse")
                    if (loginResponse != null && loginResponse.data != null) {
                        val accessToken = loginResponse.data?.token?.accessToken ?: ""
                        val refreshToken = loginResponse.data?.token?.refreshToken ?: ""

                        navigateToHomeActivity(loginResponse)
                    } else {
                        Log.e("NaverWebViewLoginActivity", "리스폰스 값 null")
                    }
                } else {
                    Log.e("NaverWebViewLoginActivity", "로그인 실패: ${response.errorBody()?.string()}")
                }
                finish()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("NaverWebViewLoginActivity", "네트워크 요청 실패", t)
                finish()
            }
        })
    }

    private fun navigateToHomeActivity(loginResponse: LoginResponse) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("login_response", loginResponse)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        Log.i("naverNavigate", "리스폰스 보냄")
        startActivity(intent)
        finish()
    }
}
