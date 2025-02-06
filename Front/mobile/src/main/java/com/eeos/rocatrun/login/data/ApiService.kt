package com.eeos.rocatrun.login.data

import retrofit2.Call
import retrofit2.http.Body
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

    @GET("/api/auth/callback/google")
    fun googleCallback(
        @Query("code") code: String
    ): Call<LoginResponse>

    @GET("/api/auth/callback/naver")
    fun naverCallback(
        @Query("code") code: String,
        @Query("state") state: String
    ): Call<LoginResponse>

    @POST("/api/auth/refresh/jwt")
    fun refreshJwtToken(
        @Body request : JwtTokenRequest
    ): Call<LoginResponse>
}

data class JwtTokenRequest(
    val refreshToken: String
)

