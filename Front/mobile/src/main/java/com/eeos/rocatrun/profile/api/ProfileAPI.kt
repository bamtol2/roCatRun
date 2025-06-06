package com.eeos.rocatrun.profile.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileAPI {

    // 회원 정보 조회
    @GET("domain/mypage")
    fun getProfileInfo(
        @Header("Authorization") authorization: String,
    ): Call<ProfileResponse>

    // 닉네임 중복 확인
    @GET("domain/characters/check-nickname/{nickname}")
    fun checkNickname(
        @Header("Authorization") authorization: String,
        @Path("nickname") nickname: String
    ): Call<NicknameCheckResponse>

    // 회원 정보 수정
    @PATCH("/domain/mypage")
    fun updateProfile(
        @Header("Authorization") authorization: String,
        @Body request: UpdateProfileRequest
    ): Call<UpdateProfileResponse>

    // 로그아웃
    @POST("domain/members/logout")
    fun userLogout(
        @Header("Authorization") authorization: String,
    ): Call<ProfileResponse>

    // 회원탈퇴
    @DELETE("/domain/members/me")
    fun memberDelete(
        @Header("Authorization") authorization: String,
    ): Call<DeleteMemberResponse>
}