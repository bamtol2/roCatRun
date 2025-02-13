package com.eeos.rocatrun.home.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeAPI {

    @GET("domain/characters/me")
    fun getHomeInfo(
        @Header("Authorization") authorization: String,
    ): Call<HomeInfoResponse>
}