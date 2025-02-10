package com.eeos.rocatrun.profile.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ProfileAPI {
    @GET("domain/mypage")
    fun getProfileInfo(
        @Header("Authorization") authorization: String,
    ): Call<ProfileResponse>
}