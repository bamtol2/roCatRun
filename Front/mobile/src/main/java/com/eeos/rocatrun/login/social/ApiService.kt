package com.eeos.rocatrun.login.social

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("/api/auth/callback/kakao")
    @FormUrlEncoded
    fun kakaoCallback(
        @Field("code") code: String
    ): Call<LoginResponse>
}