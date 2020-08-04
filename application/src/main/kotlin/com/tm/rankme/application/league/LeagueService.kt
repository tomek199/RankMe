package com.tm.rankme.application.league

import com.tm.rankme.domain.league.League

interface LeagueService {
    fun get(leagueId: String): League
    fun create(league: League): League
    fun checkIfExist(leagueId: String)
}
