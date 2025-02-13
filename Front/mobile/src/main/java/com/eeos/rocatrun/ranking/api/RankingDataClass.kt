package com.eeos.rocatrun.ranking.api

data class RankingResponse(
    val success: Boolean,
    val message: String,
    val data: RankingData
)

data class RankingData(
    val myRanking: Ranking,
    val rankings: List<Ranking>
)

data class Ranking(
    val rank: Int,
    val characterImage: String,
    val nickname: String,
    val level: Int
)