package com.tm.rankme.api.query.player

import java.util.*

data class GetPlayerQuery(val id: UUID)

data class GetPlayersQuery(val leagueId: UUID)
