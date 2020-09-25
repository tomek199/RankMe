package com.tm.rankme.domain.match

import com.tm.rankme.domain.Repository

interface MatchRepository : Repository<Match> {
    fun findByLeagueId(leagueId: String): List<Match>
}