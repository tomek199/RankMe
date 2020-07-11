package com.tm.rankme.domain.event

import com.tm.rankme.domain.Repository

interface EventRepository : Repository<Event> {
    fun findByLeagueId(leagueId: String): List<Event>
}