package com.tm.rankme.api.query.game

import java.util.*

data class GetGamesQuery(val leagueId: UUID, val first: Int, val after: String? = null)