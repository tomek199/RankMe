package com.tm.rankme.application.competitor

import java.time.LocalDate

data class CompetitorModel(
    val id: String,
    val username: String,
    val statistics: CompetitorStatisticsModel
)

data class CompetitorStatisticsModel(
    val deviation: Int,
    val rating: Int,
    val won: Int,
    val lost: Int,
    val draw: Int,
    val lastGame: LocalDate?
)

data class AddCompetitorInput(
    val leagueId: String,
    val username: String
)