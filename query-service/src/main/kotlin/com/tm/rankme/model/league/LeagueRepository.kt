package com.tm.rankme.model.league

import com.tm.rankme.model.Page

interface LeagueRepository {
    fun byId(id: String): League?
    fun store(league: League)
    fun list(first: Int, after: String? = null): Page<League>
}