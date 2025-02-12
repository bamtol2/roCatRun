package com.eeos.rocatrun.stats.api

import com.google.gson.annotations.SerializedName

// Daily Data class
data class DailyStatsResponse(
    val userId: String,
    val nickName: String?,
    val games: List<Game>
)

// 게임 데이터 클래스
data class Game(
    val roomId: String,
    val date: String,
    val difficulty: String,
    val result: Boolean,
    val players: List<Player>,
    val details: GameDetails
)

// 플레이어 데이터 클래스
data class Player(
    val rank: Int,
    val profileUrl: String,
    val nickname: String,
    val distance: Double,
    val attackCount: Int
)

// 게임 세부 정보 데이터 클래스
data class GameDetails(
    val pace: String,
    val calories: Int,
    val cadence: Double,
    val distance: Double,
    val runningTime: String
)