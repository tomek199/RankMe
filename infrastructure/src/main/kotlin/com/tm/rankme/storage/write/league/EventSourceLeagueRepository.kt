package com.tm.rankme.storage.write.league

import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.storage.write.EventEmitter
import com.tm.rankme.storage.write.EventStorage
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
open class EventSourceLeagueRepository @Autowired constructor(
    private val eventStorage: EventStorage<League>,
    private val eventEmitter: EventEmitter<League>
) : LeagueRepository {

    override fun byId(id: UUID): League = eventStorage.events(id.toString()).let {
        League.from(it)
    }

    override fun store(aggregate: League) = aggregate.pendingEvents.forEach {
        eventStorage.save(it)
        eventEmitter.emit(it)
    }
}