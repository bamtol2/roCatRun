package com.eeos.rocatrun.stats.api

// Week, Mon Data class
data class WeekMonStatsResponse(
    val status: String,
    val data: WeekMonStatsData
)

data class WeekMonStatsData(
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