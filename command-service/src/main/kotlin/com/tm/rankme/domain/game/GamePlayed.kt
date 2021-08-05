package com.tm.rankme.domain.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.base.Event
import java.time.LocalDateTime
import java.time.ZoneOffset

class GamePlayed(
    val leagueId: String,
    val firstId: String,
    val firstScore: Int,
    val firstDeviation: Int,
    val firstDeviationDelta: Int,
    val firstRating: Int,
    val firstRatingDelta: Int,
    val secondId: String,
    val secondScore: Int,
    val secondDeviation: Int,
    val secondDeviationDelta: Int,
    val secondRating: Int,
    val secondRatingDelta: Int,
    val dateTime: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    aggregateId: String = randomNanoId(),
    version: Long = 0
) : Event<Game>(aggregateId, version) {

    override val type: String = "game-played"

    override fun apply(aggregate: Game) {
        aggregate.apply(this)
    }
}