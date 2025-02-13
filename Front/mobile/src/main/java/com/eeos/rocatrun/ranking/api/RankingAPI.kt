package com.eeos.rocatrun.ranking.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface RankingAPI {
    @GET("/domain/characters/rankings")
    fun getRankingInfo(
        @Header("Authorization") authorization: String,
    ): Call<RankingResponse>
}