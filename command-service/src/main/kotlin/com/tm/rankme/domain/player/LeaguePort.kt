package com.tm.rankme.domain.player

interface LeaguePort {
    fun exist(leagueId: String): Boolean
}