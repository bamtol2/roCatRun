package com.eeos.rocatrun.login.data

import android.content.Context
import android.util.Log
import android.util.Base64
import org.json.JSONObject
import java.util.Date
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object TokenManager {

    // 만료된 토큰 갱신
    fun refreshTokensIfNeeded(context: Context, onRefreshComplete: (Boolean) -> Unit) {
        val accessToken = TokenStorage.getAccessToken(context)

        if (isAccessTokenExpired(accessToken)) {
            val refreshToken = TokenStorage.getRefreshToken(context)
            if (refreshToken != null) {
                RetrofitClient.apiService.refreshKakaoToken(refreshToken).enqueue(object : Callback<AuthTokens> {
                    override fun onResponse(call: Call<AuthTokens>, response: Response<AuthTokens>) {
                        if (response.isSuccessful) {
                            val newTokens = response.body()?.data
                            if (newTokens != null) {
                                TokenStorage.saveTokens(context, newTokens.accessToken ?: "", newTokens.refreshToken ?: "")
                                Log.i("토큰 갱신", "토큰 갱신 성공")
                                onRefreshComplete(true)  // 갱신 성공 알림
                            } else {
                                onRefreshComplete(false) // 갱신 실패
                            }
                        } else {
                            Log.e("토큰 갱신", "토큰 갱신 실패: ${response.errorBody()?.string()}")
                            onRefreshComplete(false)
                        }
                    }

                    override fun onFailure(call: Call<AuthTokens>, t: Throwable) {
                        Log.e("토큰 갱신", "네트워크 요청 실패", t)
                        onRefreshComplete(false)
                    }
                })
            } else {
                Log.e("토큰 갱신", "리프레시 토큰 없음")
                onRefreshComplete(false)
            }
        } else {
            Log.i("토큰 상태", "액세스 토큰이 아직 유효함")
            onRefreshComplete(true)  // 기존 토큰이 유효한 경우
        }
    }


    fun isTokenVaild(accessToken: String?) : Boolean{
        return accessToken != null && !isAccessTokenExpired(accessToken)
    }
    // JWT 형식의 액세스 토큰 만료 여부 확인 메서드
    private fun isAccessTokenExpired(accessToken: String?): Boolean {
        if (accessToken.isNullOrEmpty()) return true

        return try {
            val parts = accessToken.split(".")
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                val jsonPayload = JSONObject(payload)
                val exp = jsonPayload.optLong("exp", 0)

                // 현재 시간과 만료 시간 비교
                exp != 0L && exp * 1000 < Date().time
            } else {
                true  // 토큰 형식이 올바르지 않으면 만료된 것으로 간주
            }
        } catch (e: Exception) {
            Log.e("토큰 파싱 오류", "액세스 토큰 파싱 중 오류 발생", e)
            true  // 파싱 오류 시 만료된 것으로 간주
        }
    }
}