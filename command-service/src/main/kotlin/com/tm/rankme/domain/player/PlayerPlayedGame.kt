package com.tm.rankme.domain.player

import com.tm.rankme.domain.base.Event
import java.util.*

class PlayerPlayedGame(
    val deviationDelta: Int,
    val ratingDelta: Int,
    val score: Int,
    aggregateId: UUID,
    version: Long
) : Event<Player>(aggregateId, version) {

    override val type: String = "player-played-game"

    override fun apply(aggregate: Player) {
        aggregate.apply(this)
    }
}