package com.tm.rankme.domain.player

import com.tm.rankme.domain.base.EventEmitter
import com.tm.rankme.domain.base.EventStorage
import com.tm.rankme.domain.base.Repository
import java.util.*

abstract class PlayerRepository(
    private val eventStorage: EventStorage<Player>,
    private val eventEmitter: EventEmitter
) : Repository<Player> {

    override fun byId(id: UUID): Player = eventStorage.events(id.toString()).let {
        Player.from(it)
    }

    override fun store(aggregate: Player) = aggregate.pendingEvents.forEach {
        eventStorage.save(it)
        eventEmitter.emit(it)
    }
}