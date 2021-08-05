package com.tm.rankme.api.query.game

data class GetGamesForLeagueQuery(val leagueId: String, val first: Int, val after: String? = null)

data class GetGamesForPlayerQuery(val playerId: String, val first: Int,  val after: String? = null)