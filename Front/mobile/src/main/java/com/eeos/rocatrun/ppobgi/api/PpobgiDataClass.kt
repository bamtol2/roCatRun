package com.eeos.rocatrun.ppobgi.api

data class PpobgiResponse(
    val success: Boolean,
    val message: String,
    val data: DrawData
)

data class DrawData(
    val drawnItems: List<DrawItem>,
    val remainingCoins: Int
)

data class DrawItem(
    val id: Int,
    val name: String,
    val koreanName: String,
    val description: String,
    val rarity: String
)
