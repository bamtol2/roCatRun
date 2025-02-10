package com.eeos.rocatrun.home.api


data class HomeInfoResponse(
    val status: Boolean,
    val message: String,
    val data: UserData
)

data class UserData(
    val id: Int,
    val nickname: String,
    val level: Int,
    val experience: Long,
    val characterImage: String,
    val coin: Int,
    val totalGames: Int,
    val wins: Int,
    val losses: Int
)