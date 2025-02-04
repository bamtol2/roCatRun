package com.eeos.rocatrun.login.social

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/api/auth/callback/kakao")
    fun kakaoCallback(@Query("code") code: String): Call<LoginResponse>
}