package com.eeos.rocatrun.shop.api

data class SellItemsRequest(
    val inventoryIds: List<Int>,
    val totalPrice: Int
)

data class SellItemsResponse(
    val success: Boolean,
    val message: String,
    val data: SoldItemData
)

data class SoldItemData(
    val soldInventoryId: Int,
    val receivedCoins: Int,
    val currentCoins: Int
)