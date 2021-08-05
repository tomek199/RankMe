package com.tm.rankme.infrastructure.game

internal data class Played(
    val type: String,
    val aggregateId: String,
    val version: Long,
    val timestamp: Long,
    val leagueId: String,
    val dateTime: Long,
    val firstId: String,
    val firstScore: Int,
    val firstDeviation: Int,
    val firstDeviationDelta: Int,
    val firstRating: Int,
    val firstRatingDelta: Int,
    val secondId: String,
    val secondScore: Int,
    val secondDeviation: Int,
    val secondDeviationDelta: Int,
    val secondRating: Int,
    val secondRatingDelta: Int
)

internal data class Scheduled(
    val type: String,
    val aggregateId: String,
    val version: Long,
    val timestamp: Long,
    val leagueId: String,
    val dateTime: Long,
    val firstId: String,
    val secondId: String
)