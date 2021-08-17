package com.tm.rankme.model.league

interface LeagueRepository {
    fun byId(id: String): League?
    fun store(league: League)
}