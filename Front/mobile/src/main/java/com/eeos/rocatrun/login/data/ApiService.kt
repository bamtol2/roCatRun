package com.eeos.rocatrun.login.data

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("/api/auth/callback/kakao")
    fun kakaoCallback(
        @Query("code") code: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("/api/auth/refresh/kakao")
    fun refreshKakaoToken(
        @Field("refreshToken") refreshToke: String
    ): Call<AuthTokens>

    @GET("/api/auth/callback/google")
    fun googleCallback(
        @Query("code") code: String
    ): Call<LoginResponse>

    @GET("/api/auth/callback/naver")
    fun naverCallback(
        @Query("code") code: String,
        @Query("state") state: String
    ): Call<LoginResponse>
}

