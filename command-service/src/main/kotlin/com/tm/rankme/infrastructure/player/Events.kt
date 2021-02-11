package com.tm.rankme.infrastructure.player

import java.util.*

internal data class Created(
    val type: String,
    val aggregateId: UUID,
    val version: Long,
    val timestamp: Long,
    val leagueId: UUID,
    val name: String,
    val deviation: Int,
    val rating: Int
)

internal data class PlayedGame(
    val type: String,
    val aggregateId: UUID,
    val version: Long,
    val timestamp: Long,
    val deviationDelta: Int,
    val ratingDelta: Int,
    val score: Int
)