package com.eeos.rocatrun.ppobgi.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PpobgiAPI {
    @POST("/domain/items/draw")
    suspend fun randomPpobgi(
        @Header("Authorization") authorization: String,
        @Body drawRequest: DrawRequest
    ): Response<PpobgiResponse>
}

data class DrawRequest(
    val drawCount: Int
)