package com.tm.rankme.domain.game

import com.tm.rankme.domain.base.Event
import java.util.*

class GameScheduled(
    val leagueId: UUID,
    val firstId: UUID,
    val secondId: UUID,
    aggregateId: UUID = UUID.randomUUID()
) : Event<Game>(aggregateId, 0) {
    override val type: String = "game-scheduled"

    override fun apply(aggregate: Game) {
        aggregate.apply(this)
    }
}