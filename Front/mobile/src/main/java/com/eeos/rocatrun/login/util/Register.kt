package com.eeos.rocatrun.login.util

import android.util.Log
import com.eeos.rocatrun.login.data.CreateCharacterRequest
import com.eeos.rocatrun.login.data.RetrofitClient

object Register {

    suspend fun registerCharacter(nickname: String, token: String): Boolean {
        return try {
            val response = RetrofitClient.apiService.createCharacter(
                "Bearer $token",
                CreateCharacterRequest(nickname)
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i("캐릭터 생성", "성공: ${it.success}")
                    it.success
                } ?: false
            } else {
                Log.e("캐릭터 생성", "API 호출 실패: ${response.code()} - ${response.errorBody()?.string() ?: "본문 없음"}")
                false
            }
        } catch (e: Exception) {
            Log.e("캐릭터 생성", "예외 발생", e)
            false
        }
    }
}
