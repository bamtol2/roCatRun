package com.eeos.rocatrun.stats.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface StatsAPI {
    @GET("/api/statistics/running-stats/daily")
    fun getDailyStats(
        @Header("Authorization") authorization: String,
    ): Call<DailyStatsResponse>

    @GET("/api/statistics/running-stats/weekly")
    fun getWeekStats(
        @Header("Authorization") authorization: String,
        @Query("date") date: String,
        @Query("week") week: Int
    ): Call<WeekMonStatsResponse>

    @GET("/api/statistics/running-stats/monthly")
    fun getMonStats(
        @Header("Authorization") authorization: String,
        @Query("date") date: String
    ): Call<WeekMonStatsResponse>
}