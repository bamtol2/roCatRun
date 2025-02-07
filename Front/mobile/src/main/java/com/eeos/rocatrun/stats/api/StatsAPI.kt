package com.eeos.rocatrun.stats.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface StatsAPI {
    @GET("api/stats/daily")
    fun getDailyStats(): Call<DailyStatsResponse>

    @GET("api/stats/week")
    fun getWeekStats(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("week") week: Int
    ): Call<WeekMonStatsResponse>

    @GET("api/stats/mon")
    fun getMonStats(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): Call<WeekMonStatsResponse>
}