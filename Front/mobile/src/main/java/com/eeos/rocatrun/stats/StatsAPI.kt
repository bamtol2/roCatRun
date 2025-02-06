package com.eeos.rocatrun.stats

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET

// 최상위 응답 데이터 클래스
data class StatsResponse(
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


interface StatsAPI {
    @GET("api/stats/daily")
    fun getDailyStats(): Call<StatsResponse>
}