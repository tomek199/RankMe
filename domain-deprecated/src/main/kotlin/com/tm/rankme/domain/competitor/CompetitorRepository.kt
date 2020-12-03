package com.tm.rankme.domain.competitor

import com.tm.rankme.domain.Repository

interface CompetitorRepository : Repository<Competitor> {
    fun findByLeagueId(leagueId: String): List<Competitor>
}