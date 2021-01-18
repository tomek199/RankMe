package com.tm.rankme.domain.game

import com.tm.rankme.domain.base.EventEmitter
import com.tm.rankme.domain.base.EventStorage
import com.tm.rankme.domain.base.Repository
import java.util.*

abstract class GameRepository(
    private val eventStorage: EventStorage<Game>,
    private val eventEmitter: EventEmitter
) : Repository<Game> {

    override fun byId(id: UUID): Game = eventStorage.events(id.toString()).let {
        Game.from(it)
    }

    override fun store(aggregate: Game) = aggregate.pendingEvents.forEach {
        eventStorage.save(it)
        eventEmitter.emit(it)
    }
}