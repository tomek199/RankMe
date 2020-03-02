package com.tm.rankme.application.competitor

data class CompetitorModel(
        val id: String,
        val username: String,
        val leagueId: String,
        val statistics: CompetitorStatisticsModel
)

data class CompetitorStatisticsModel(
        val deviation: Int,
        val rating: Int,
        val won: Int,
        val lost: Int,
        val draw: Int,
        val lastGame: String?
)