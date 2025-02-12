package com.eeos.rocatrun.login.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.eeos.rocatrun.login.data.CreateCharacterRequest
import com.eeos.rocatrun.login.data.RetrofitClient

object Register {

    suspend fun registerCharacter(
        context: Context,
        nickname: String,
        token: String,
        age : Int,
        height : Int,
        weight : Int,
        gender : String
    ): Boolean {
        return try {

            val response = RetrofitClient.apiService.createCharacter(
                "Bearer $token",
                CreateCharacterRequest(nickname,age,gender, height,weight)
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i("캐릭터 생성", "성공: ${it.success}")
                    it.success
                } ?: false
            } else {
                Log.e("캐릭터 생성", "API 호출 실패: ${response.code()} - ${response.errorBody()?.string() ?: "본문 없음"}")
                Toast.makeText(context, "이미 가입한 회원이거나 사용할 수 없는 닉네임입니다..", Toast.LENGTH_SHORT).show()
                false
            }
        } catch (e: Exception) {
            Log.e("캐릭터 생성", "예외 발생", e)
            false
        }
    }
}
