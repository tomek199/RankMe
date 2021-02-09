package com.tm.rankme.infrastructure.game

import java.util.*

internal data class Played(
    val type: String,
    val aggregateId: UUID,
    val version: Long,
    val timestamp: Long,
    val leagueId: UUID,
    val dateTime: Long,
    val firstId: UUID,
    val firstScore: Int,
    val firstDeviationDelta: Int,
    val firstRatingDelta: Int,
    val secondId: UUID,
    val secondScore: Int,
    val secondDeviationDelta: Int,
    val secondRatingDelta: Int
)

internal data class Scheduled(
    val type: String,
    val aggregateId: UUID,
    val version: Long,
    val timestamp: Long,
    val leagueId: UUID,
    val dateTime: Long,
    val firstId: UUID,
    val secondId: UUID
)