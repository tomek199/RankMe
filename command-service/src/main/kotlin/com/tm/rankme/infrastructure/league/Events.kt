package com.tm.rankme.infrastructure.league

internal data class Created(
    val type: String,
    val aggregateId: String,
    val version: Long,
    val timestamp: Long,
    val name: String,
    val allowDraws: Boolean = false,
    val maxScore: Int = 2
)

internal data class Renamed(
    val type: String,
    val aggregateId: String,
    val version: Long,
    val timestamp: Long,
    val name: String
)

internal data class SettingsChanged(
    val type: String,
    val aggregateId: String,
    val version: Long,
    val timestamp: Long,
    val allowDraws: Boolean = false,
    val maxScore: Int = 2
)