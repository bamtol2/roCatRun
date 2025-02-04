package com.eeos.rocatrun.login.social

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.os.Bundle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KaKaoRedirectHandler : Activity(){
    companion object{
        private var isCodeHandled = false // 인가 코드가 이미 처리되었는지 확인하는 변수
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(isCodeHandled){
            finish()
            return
        }

        val data: Uri? = intent?.data
        val code = data?.getQueryParameter("code")

        if(code != null){
            Log.i("인가코드 성공", "인가코드 : ${code}" )
            sendAuthCodeToBackend(code.toString())

        }else{
            Log.e("인가코드 실패", "인가코드 없음")
        }

        finish()
    }
    private fun sendAuthCodeToBackend(code: String) {
        val apiService = RetrofitClient.apiService

        apiService.kakaoCallback(code).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    Log.i("Auth", "서버 응답 성공: ${response.body()?.message}")
                } else {
                    Log.e("Auth", "서버 응답 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Auth", "서버 요청 실패", t)
            }
        })
    }
}