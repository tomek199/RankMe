package com.tm.rankme.domain.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.base.Event

class GameScheduled(
    val leagueId: String,
    val firstId: String,
    val secondId: String,
    val dateTime: Long,
    aggregateId: String = randomNanoId()
) : Event<Game>(aggregateId, 0) {

    override val type: String = "game-scheduled"

    override fun apply(aggregate: Game) {
        aggregate.apply(this)
    }
}