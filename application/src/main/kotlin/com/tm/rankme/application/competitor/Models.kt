package com.tm.rankme.application.competitor

import java.time.LocalDate

data class CompetitorModel(
    val id: String,
    val username: String,
    val deviation: Int,
    val rating: Int,
    val lastGame: LocalDate?
)

data class AddCompetitorInput(
    val leagueId: String,
    val username: String
)