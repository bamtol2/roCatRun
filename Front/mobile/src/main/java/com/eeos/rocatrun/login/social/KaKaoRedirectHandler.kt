package com.eeos.rocatrun.login.social

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.os.Bundle
import com.eeos.rocatrun.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KaKaoRedirectHandler : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("시작","시작 됐음")

        val data: Uri? = intent?.data
        Log.i("데이터", "Intent data:$data")
        val code = data?.getQueryParameter("code")

        if (code != null) {
            Log.i("코드 확인", "인가 코드 받음 : $code")
            // 백엔드로 인가 코드 전달
            sendAuthCodeToBackend(code)
        } else {
            Log.e("KaKaoRedirectHandler", "Authorization code missing")
        }

        finish()  // 처리 후 액티비티 종료
    }

    private fun sendAuthCodeToBackend(code: String) {
        val apiService = RetrofitClient.apiService
        apiService.kakaoCallback(code).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.i("KaKaoRedirectHandler", "Login successful: ${loginResponse?.message}")
                    // 결과를 LoginActivity로 전달
                    val intent = Intent(this@KaKaoRedirectHandler, LoginActivity::class.java).apply {
                        putExtra("login_response", loginResponse)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                    startActivity(intent)
                } else {
                    Log.e("KaKaoRedirectHandler", "Login failed: ${response.errorBody()?.string()}")
                }
                finish()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("KaKaoRedirectHandler", "Network request failed", t)
                finish()
            }
        })
    }
}
