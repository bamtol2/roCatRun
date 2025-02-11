package com.eeos.rocatrun.login.data

import android.provider.ContactsContract.CommonDataKinds.Nickname
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
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

    @GET("/domain/characters/check-nickname/{nickname}")
    suspend fun checkNickname(
        @Header("Authorization") authorization: String,
        @Path("nickname") nickname: String
    ): Response<NicknameCheckResponse>

    @POST("/domain/characters")
    suspend fun createCharacter(
        @Header("Authorization") authorization: String,
        @Body request: CreateCharacterRequest
    ): Response<CreateCharacterResponse>

    @GET("/domain/characters/me")
    suspend fun checkMember(
        @Header("Authorization") authorization: String
    ) :Response<MemberResponse>
}

data class JwtTokenRequest(
    val refreshToken: String
)
data class CreateCharacterRequest(
    val nickname: String,
    val age : Int,
    val gender : String,
    val height : Int,
    val weight : Int
)
