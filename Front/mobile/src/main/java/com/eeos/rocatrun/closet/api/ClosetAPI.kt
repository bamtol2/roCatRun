package com.eeos.rocatrun.closet.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ClosetAPI {
    @Multipart
    @POST("/domain/upload/character-image")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>

    @GET("/domain/inventory/items")
    suspend fun getAllInventory(
        @Header("Authorization") token: String,
    ): Response<InventoryResponse>

    @PUT("/domain/inventory/items/equip")
    suspend fun putItemsEquip(
        @Header("Authorization") token: String,
        @Body requestBody: EquipRequest
    ): Response<InventoryResponse>

    @GET("/domain/items")
    suspend fun getAllItems(
        @Header("Authorization") token: String,
    ): Response<ItemResponse>
}