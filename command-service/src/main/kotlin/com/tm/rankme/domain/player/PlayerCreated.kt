package com.tm.rankme.domain.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.base.Event

class PlayerCreated(
    val leagueId: String,
    val name: String,
    val deviation: Int = 350,
    val rating: Int = 1500,
    aggregateId: String = randomNanoId()
) : Event<Player>(aggregateId, 0) {

    override val type: String = "player-created"

    override fun apply(aggregate: Player) {
        aggregate.apply(this)
    }
}