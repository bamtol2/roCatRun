package com.eeos.rocatrun.login.data

import android.content.Context
import android.content.SharedPreferences

object TokenStorage {
    // 모든 함수에서 PREF_NAME을 통해 접근할 수 있음
    private const val PREF_NAME = "rocatrun_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val REFRESH_TOKEN_KEY = "refresh_token"
    // MODE_PRIVATE로 해당 앱만 접근할 수 있게 설정
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    // putString()을 통해 토큰 값을 저장하고, apply()로 비동기 저장 완료
    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        val editor = getPreferences(context).edit()
        editor.putString(ACCESS_TOKEN_KEY, accessToken)
        editor.putString(REFRESH_TOKEN_KEY, refreshToken)
        editor.apply()
    }
    // 저장된 토큰 값 불러오는 함수들
    fun getAccessToken(context: Context): String? {
        return getPreferences(context).getString(ACCESS_TOKEN_KEY, null)
    }

    fun getRefreshToken(context: Context): String? {
        return getPreferences(context).getString(REFRESH_TOKEN_KEY, null)
    }

    // 토큰 값 삭제하는 함수(로그아웃시 사용)
    fun clearTokens(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(ACCESS_TOKEN_KEY)
        editor.remove(REFRESH_TOKEN_KEY)
        editor.apply()
    }
}
