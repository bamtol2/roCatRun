package com.eeos.rocatrun.closet.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ClosetAPI {
    @Multipart
    @POST("/domain/upload")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>

    @GET("/domain/inventory/items")
    suspend fun getAllItems(
        @Header("Authorization") token: String,
    ): Response<InventoryResponse>

    @GET("/domain/inventory/items")
    suspend fun getCategoryItems(
        @Header("Authorization") token: String,
        @Query("category") category: String
    ): Response<InventoryResponse>
}