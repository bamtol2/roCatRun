package com.eeos.rocatrun.login.util

import android.util.Log
import com.eeos.rocatrun.login.data.RetrofitClient


object NicknameCheckHelper {

    // 닉네임 체크 함수 (코루틴 사용)
    suspend fun checkNicknameAvailability(nickname: String, token: String): Boolean? {
        return try {
            val response = RetrofitClient.apiService.checkNickname("Bearer $token", nickname)
            if (response.isSuccessful) {
                response.body()?.let { nicknameResponse ->
                    if (nicknameResponse.success) {
                        Log.i("닉네임 체크", "닉네임 확인 성공: ${nicknameResponse.data}")
                        nicknameResponse.data  // 중복 여부 반환
                    } else {
                        Log.e("닉네임 체크", "응답 상태 실패: ${nicknameResponse.message}")
                        null
                    }
                }
            } else {
                Log.e("닉네임 체크", "API 호출 실패: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("닉네임 체크", "예외 발생: ${e.message}")
            null
        }
    }
}
