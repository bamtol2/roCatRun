package com.eeos.rocatrun.login.social

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat


object KakaoLoginHandler{
    private const val KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize"
    private const val CLIENT_ID = "08554835b2f79b10c4673f267862ac7f"
    private const val REDIRECT_URI = "http://i12e205.p.ssafy.io:8080/api/auth/callback/kakao"


    fun performLogin(context: Context,
                     onSuccess : (String) -> Unit,
                     onError : (Throwable) -> Unit){
        try{
            val authorizationUri = Uri.Builder()
                .scheme("https")
                .authority("kauth.kakao.com")
                .path("/oauth/authorize")
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter("response_type", "code")
                .build()
            val browserIntent = Intent(Intent.ACTION_VIEW, authorizationUri)
            ContextCompat.startActivity(context, browserIntent, null)
        } catch (e: Exception){
            Log.e("카카오 로그인 시도", "로그인 오류", e)
            onError(e)
        }


    }
}
