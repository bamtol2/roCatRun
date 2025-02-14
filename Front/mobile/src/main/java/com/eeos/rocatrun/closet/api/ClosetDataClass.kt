package com.eeos.rocatrun.closet.api

data class UploadResponse(
    val imageUrl: String
)

data class InventoryResponse(
    val success: Boolean,
    val message: String,
    val data: List<InventoryItem>
)

data class InventoryItem(
    val inventoryId: Int,
    val itemName: String,
    val description: String?,
    val listImage: String?,
    val equipImage: String?,
    val listImageIsGif: Boolean,
    val equipImageIsGif: Boolean,
    val category: String,
    val rarity: String,
    val price: Int,
    val equipped: Boolean
)