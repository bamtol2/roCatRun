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

class GoogleWebViewLoginActivity : Activity() {

    private lateinit var webView: WebView

    companion object {
        private const val GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val CLIENT_ID = "38473355620-38hgd5mqor7ruv519urhtf9c59qedtcd.apps.googleusercontent.com"
        private const val REDIRECT_URI = "https://i12e205.p.ssafy.io:8080/api/auth/callback/google"
        private const val SCOPE = "email profile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_login)
        Log.i("구글", "구글 시작")

        webView = findViewById(R.id.webView)
        webView.settings.apply {
            javaScriptEnabled = true

            // User-Agent 변경 (모바일 브라우저처럼 설정)
            userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Mobile Safari/537.36"
        }

        // OAuth 로그인 페이지 로드
        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("accounts.google.com")
            .path("/o/oauth2/v2/auth")
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", SCOPE)
            .appendQueryParameter("access_tpye", "offline")
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

        if (code != null) {
            Log.i("GoogleWebViewLoginActivity", "인가코드 수신: $code")
            sendAuthCodeToBackend(code)
        } else {
            Log.e("GoogleWebViewLoginActivity", "인가 코드 없음")
            finish()
        }
    }

    private fun sendAuthCodeToBackend(code: String) {
        if (code.isNullOrEmpty()) {
            Log.e("GoogleWebViewLoginActivity", "code 값이 null")
            finish()
            return
        }

        val apiService = RetrofitClient.apiService
        apiService.googleCallback(code).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.i("google", "로그인 성공, response: $loginResponse")
                    if (loginResponse != null && loginResponse.data != null) {
                        val accessToken = loginResponse.data.token?.accessToken ?: ""
                        val refreshToken = loginResponse.data.token?.refreshToken ?: ""
                        TokenStorage.saveTokens(this@GoogleWebViewLoginActivity, accessToken, refreshToken)
                        val pref = getSharedPreferences("rocatrun_prefs", MODE_PRIVATE)
                        val value1 = pref.getString("access_token", "저장된 엑세스 토큰")
                        val value2 = pref.getString("refresh_token", "저장된 리프레시 토큰")
                        Log.i("저장된 accessToken", "accessToken: $value1")
                        Log.i("저장된 refreshToken", "refreshToken: $value2")

                        navigateToHomeActivity(loginResponse)
                    } else {
                        Log.e("GoogleWebViewLoginActivity", "리스폰스 값 null")
                    }
                } else {
                    Log.e("GoogleWebViewLoginActivity", "로그인 실패: ${response.errorBody()?.string()}")
                }
                finish()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("GoogleWebViewLoginActivity", "네트워크 요청 실패", t)
                finish()
            }
        })
    }

    private fun navigateToHomeActivity(loginResponse: LoginResponse) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("login_response", loginResponse)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        Log.i("GoogleNavigate", "리스폰스 보냄")
        startActivity(intent)
        finish()
    }
}
