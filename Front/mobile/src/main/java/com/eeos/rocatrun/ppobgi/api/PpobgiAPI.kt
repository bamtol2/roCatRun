package com.eeos.rocatrun.ppobgi.api

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface PpobgiAPI {
    @POST("/domain/items/draw")
    @FormUrlEncoded
    suspend fun randomPpobgi(
        @Header("Authorization") authorization: String,
        @Field("drawCount") drawCount: Int
    ): Response<PpobgiResponse>
}