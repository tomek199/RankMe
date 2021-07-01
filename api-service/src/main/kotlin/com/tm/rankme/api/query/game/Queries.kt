package com.tm.rankme.api.query.game

import java.util.*

data class GetGamesForLeagueQuery(val leagueId: UUID, val first: Int, val after: String? = null)

data class GetGamesForPlayerQuery(val playerId: UUID, val first: Int,  val after: String? = null)