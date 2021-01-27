package com.tm.rankme.domain.league

import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.base.EventStorage
import com.tm.rankme.domain.base.Repository
import java.util.*

abstract class LeagueRepository(
    protected val eventStorage: EventStorage<League>,
    protected val eventBus: EventBus
) : Repository<League> {

    override fun byId(id: UUID): League = eventStorage.events(id.toString()).let {
        League.from(it)
    }

    override fun store(aggregate: League) = aggregate.pendingEvents.forEach {
        eventStorage.save(it)
        eventBus.emit(it)
    }

    abstract fun exist(id: UUID): Boolean
}