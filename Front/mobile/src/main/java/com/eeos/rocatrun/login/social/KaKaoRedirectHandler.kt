package com.eeos.rocatrun.login.social

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.eeos.rocatrun.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KaKaoRedirectHandler : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("KaKaoRedirectHandler", "시작")

        val data: Uri? = intent?.data
        Log.i("KaKaoRedirectHandler", "데이터 받음: $data")
        val code = data?.getQueryParameter("code")

        if (code != null) {
            Log.i("KaKaoRedirectHandler", "인가코드 받음: $code")
            sendAuthCodeToBackend(code)  // 인가 코드 전달 및 로그인 응답 처리
            Log.i("백엔드로 인가 코드 보내기", "함수 실행")
        } else {
            Log.e("KaKaoRedirectHandler", "인가코드 못받음")
            finish()
        }
    }

    private fun sendAuthCodeToBackend(code: String) {
        Log.i("함수 실행" , "성공")
        val apiService = RetrofitClient.apiService
        apiService.kakaoCallback(code).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.i("KaKaoRedirectHandler", "로그인 성공, response: $loginResponse")

                    if (loginResponse != null) {
                        navigateToLoginActivity(loginResponse)  // 응답을 인텐트에 담아 전달
                    } else {
                        Log.e("KaKaoRedirectHandler", "리스폰스 값 null")
                    }
                } else {
                    Log.e("KaKaoRedirectHandler", "로그인 실패: ${response.errorBody()?.string()}")
                }
                finish()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("KaKaoRedirectHandler", "네트워크 연결 실패", t)
                finish()
            }
        })
    }

    private fun navigateToLoginActivity(loginResponse: LoginResponse) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("login_response", loginResponse)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        Log.i("KaKaoRedirectHandler", "Navigating to LoginActivity with response")
        startActivity(intent)
    }
}
