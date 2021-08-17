package com.tm.rankme.infrastructure.player

internal data class Created(
    val type: String,
    val aggregateId: String,
    val version: Long,
    val timestamp: Long,
    val leagueId: String,
    val name: String,
    val deviation: Int,
    val rating: Int
)

internal data class PlayedGame(
    val type: String,
    val aggregateId: String,
    val version: Long,
    val timestamp: Long,
    val deviationDelta: Int,
    val ratingDelta: Int,
    val score: Int
)