package com.tm.rankme.application.league

interface LeagueService {
    fun get(leagueId: String): LeagueModel
    fun create(name: String): LeagueModel
    fun checkIfExist(leagueId: String)
}
