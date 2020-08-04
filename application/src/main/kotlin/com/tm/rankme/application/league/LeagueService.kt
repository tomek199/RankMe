package com.tm.rankme.application.league

import com.tm.rankme.domain.league.League

interface LeagueService {
    fun getLeague(leagueId: String): League
    fun saveLeague(league: League): League
    fun checkIfExist(leagueId: String)
}
