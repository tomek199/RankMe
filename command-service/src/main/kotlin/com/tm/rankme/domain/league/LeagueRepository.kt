package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.Repository

interface LeagueRepository : Repository<League> {
    fun exist(id: String): Boolean
}