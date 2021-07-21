package com.tm.rankme.domain.game

import com.tm.rankme.domain.base.Event
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class GamePlayed(
    val leagueId: UUID,
    val firstId: UUID,
    val firstScore: Int,
    val firstDeviation: Int,
    val firstDeviationDelta: Int,
    val firstRating: Int,
    val firstRatingDelta: Int,
    val secondId: UUID,
    val secondScore: Int,
    val secondDeviation: Int,
    val secondDeviationDelta: Int,
    val secondRating: Int,
    val secondRatingDelta: Int,
    val dateTime: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    aggregateId: UUID = UUID.randomUUID(),
    version: Long = 0
) : Event<Game>(aggregateId, version) {

    override val type: String = "game-played"

    override fun apply(aggregate: Game) {
        aggregate.apply(this)
    }
}