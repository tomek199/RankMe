package com.tm.rankme.subscription

data class PlayerCreated(
    val id: String,
    val leagueId: String,
    val name: String,
    val deviation: Int,
    val rating: Int
)

data class LeagueCreated(
    val id: String,
    var name: String,
    var allowDraws: Boolean,
    var maxScore: Int
)