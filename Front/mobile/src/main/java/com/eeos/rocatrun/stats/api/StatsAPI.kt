package com.eeos.rocatrun.stats.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Daily Data class
data class DailyStatsResponse(
    @SerializedName("userId") val date: String,
    @SerializedName("games") val games: List<Game>
)

// 게임 데이터 클래스
data class Game(
    @SerializedName("date") val date: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("result") val result: Boolean,
    @SerializedName("players") val players: List<Player>,
    @SerializedName("details") val details: GameDetails
)

// 플레이어 데이터 클래스
data class Player(
    @SerializedName("rank") val rank: Int,
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("distance") val distance: String,
    @SerializedName("attackCount") val attackCount: String
)

// 게임 세부 정보 데이터 클래스
data class GameDetails(
    @SerializedName("pace") val pace: String,
    @SerializedName("calories") val calories: String,
    @SerializedName("cadence") val cadence: String,
    @SerializedName("distance") val distance: String,
    @SerializedName("time") val time: String
)

// ----------------------------------------------

// Week Data class
data class WeekStatsResponse(
    val status: String,
    val data: WeeklyStatsData
)

data class WeeklyStatsData(
    val summary: SummaryStats,
    val dailyStats: List<DailyStat>
)

data class SummaryStats(
    val totalDistance: Double,
    val totalRuns: Int,
    val averagePace: String,
    val totalTime: String
)

data class DailyStat(
    val date: String,
    val distance: Double
)


interface StatsAPI {
    @GET("api/stats/daily")
    fun getDailyStats(): Call<DailyStatsResponse>

    @GET("api/stats/week")
    fun getWeekStats(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("week") week: Int
    ): Call<WeekStatsResponse>
}