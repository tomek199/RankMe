package com.tm.rankme.model.league

import java.util.*

interface LeagueRepository {
    fun byId(id: UUID): League?
    fun store(league: League)
}