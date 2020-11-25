package com.tm.rankme.infrastructure.league

import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.infrastructure.EventStorage
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class EventSourceLeagueRepository @Autowired constructor(
    private val eventStorage: EventStorage<League>
) : LeagueRepository {

    override fun byId(id: UUID): League {
        val events = eventStorage.events(id.toString())
        return League.from(events)
    }

    override fun store(aggregate: League) = aggregate.pendingEvents.forEach(eventStorage::save)
}