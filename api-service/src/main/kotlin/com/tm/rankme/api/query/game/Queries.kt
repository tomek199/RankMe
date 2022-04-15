package com.tm.rankme.api.query.game

data class GetGamesForLeagueQuery(
    val leagueId: String,
    val first: Int,
    val after: String? = null,
    val before: String? = null
)

data class GetCompletedGamesForLeagueQuery(
    val leagueId: String,
    val first: Int,
    val after: String? = null,
    val before: String? = null
)

data class GetScheduledGamesForLeagueQuery(
    val leagueId: String,
    val first: Int,
    val after: String? = null,
    val before: String? = null
)

data class GetGamesForPlayerQuery(
    val playerId: String,
    val first: Int,
    val after: String? = null,
    val before: String? = null
)

data class GetCompletedGamesForPlayerQuery(
    val playerId: String,
    val first: Int,
    val after: String? = null,
    val before: String? = null
)

data class GetScheduledGamesForPlayerQuery(
    val playerId: String,
    val first: Int,
    val after: String? = null,
    val before: String? = null
)