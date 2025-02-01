package com.eeos.rocatrun.login.social

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

object KakaoLoginHandler {

    fun performLogin(context: Context, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                handleResult(token, error, onSuccess, onError)
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                handleResult(token, error, onSuccess, onError)
            }
        }
    }

    private fun handleResult(token: OAuthToken?, error: Throwable?, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        if (error != null) {
            Log.e("KakaoLoginHandler", "카카오 로그인 실패", error)
            onError(error)
        } else if (token != null) {
            Log.i("KakaoLoginHandler", "카카오 로그인 성공, 토큰: ${token.accessToken}")
            onSuccess(token.accessToken)
        }
    }
}
