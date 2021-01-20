package com.tm.rankme.domain.game

import com.tm.rankme.domain.base.Event
import java.time.Instant
import java.util.*

class GamePlayed(
    val leagueId: UUID,
    val firstId: UUID,
    val firstScore: Int,
    val firstDeviationDelta: Int,
    val firstRatingDelta: Int,
    val secondId: UUID,
    val secondScore: Int,
    val secondDeviationDelta: Int,
    val secondRatingDelta: Int,
    val dateTime: Long = Instant.now().toEpochMilli(),
    aggregateId: UUID = UUID.randomUUID()
) : Event<Game>(aggregateId, 0) {

    override val type: String = "game-played"

    override fun apply(aggregate: Game) {
        aggregate.apply(this)
    }
}