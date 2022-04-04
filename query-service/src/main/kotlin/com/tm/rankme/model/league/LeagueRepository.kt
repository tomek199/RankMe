package com.tm.rankme.model.league

import com.tm.rankme.model.Page

interface LeagueRepository {
    fun byId(id: String): League?
    fun store(league: League)
    fun list(first: Int): Page<League>
    fun listAfter(first: Int, after: String): Page<League>
    fun listBefore(first: Int, before: String): Page<League>
}