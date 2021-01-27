package com.tm.rankme.domain.game

import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.base.EventStorage
import com.tm.rankme.domain.base.Repository
import java.util.*

abstract class GameRepository(
    private val eventStorage: EventStorage<Game>,
    private val eventBus: EventBus
) : Repository<Game> {

    override fun byId(id: UUID): Game = eventStorage.events(id.toString()).let {
        Game.from(it)
    }

    override fun store(aggregate: Game) = aggregate.pendingEvents.forEach {
        eventStorage.save(it)
        eventBus.emit(it)
    }
}