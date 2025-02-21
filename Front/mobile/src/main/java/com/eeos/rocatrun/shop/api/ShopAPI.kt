package com.eeos.rocatrun.shop.api

import com.eeos.rocatrun.closet.api.InventoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ShopAPI {
    // 모든 인벤토리 조회
    @GET("/domain/inventory/items")
    suspend fun getAllInventoryShop(
        @Header("Authorization") token: String,
    ): Response<InventoryResponse>

    // 아이템 판매
    @POST("/domain/inventory/items/sell-multiple")
    suspend fun sellItems(
        @Header("Authorization") token: String,
        @Body request: SellItemsRequest
    ): Response<SellItemsResponse>
}