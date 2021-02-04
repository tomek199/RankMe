package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.Repository
import java.util.*

interface LeagueRepository : Repository<League> {
    fun exist(id: UUID): Boolean
}