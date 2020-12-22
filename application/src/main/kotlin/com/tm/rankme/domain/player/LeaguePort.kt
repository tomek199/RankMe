package com.tm.rankme.domain.player

import java.util.*

interface LeaguePort {
    fun exist(leagueId: UUID): Boolean
}