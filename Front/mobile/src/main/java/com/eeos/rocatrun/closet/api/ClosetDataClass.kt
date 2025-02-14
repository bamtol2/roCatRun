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
    val id: Int,
    val name: String,
    val koreanName: String,
    val description: String,
    val isGif: Boolean,
    val category: String,
    val rarity: String,
    val price: Int,
    val equipped: Boolean
)
