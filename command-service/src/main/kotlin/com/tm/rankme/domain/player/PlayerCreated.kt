package com.tm.rankme.domain.player

import com.tm.rankme.domain.base.Event
import java.util.*

class PlayerCreated(
    val leagueId: UUID,
    val name: String,
    val deviation: Int = 350,
    val rating: Int = 1500,
    aggregateId: UUID = UUID.randomUUID()
) : Event<Player>(aggregateId, 0) {

    override val type: String = "player-created"

    override fun apply(aggregate: Player) {
        aggregate.apply(this)
    }
}