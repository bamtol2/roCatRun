package com.eeos.rocatrun.profile.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ProfileAPI {

    // 회원 정보 조회
    @GET("domain/mypage")
    fun getProfileInfo(
        @Header("Authorization") authorization: String,
    ): Call<ProfileResponse>

    // 로그아웃
    @POST("domain/members/logout")
    fun userLogout(
        @Header("Authorization") authorization: String,
    ): Call<ProfileResponse>
}