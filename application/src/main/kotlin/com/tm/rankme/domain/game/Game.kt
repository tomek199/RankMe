package com.tm.rankme.domain.game

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class Game private constructor() : AggregateRoot() {
    val pendingEvents = mutableListOf<Event<Game>>()
    lateinit var leagueId: UUID
        private set
    lateinit var playerIds: Pair<UUID, UUID>
        private set
    lateinit var dateTime: LocalDateTime
        private set
    var result: Pair<Result, Result>? = null
        private set

    companion object {
        fun played(leagueId: UUID, firstId: UUID, secondId: UUID,
                   firstResult: Result, secondResult: Result): Game {
            val game = Game()
            val event = GamePlayed(leagueId = leagueId,
                firstId = firstId, firstScore = firstResult.score,
                firstDeviationDelta = firstResult.deviationDelta, firstRatingDelta = firstResult.ratingDelta,
                secondId = secondId, secondScore = secondResult.score,
                secondDeviationDelta = secondResult.deviationDelta, secondRatingDelta = secondResult.ratingDelta
            )
            game.add(event)
            return game
        }

        fun scheduled(leagueId: UUID, dateTime: LocalDateTime, firstId: UUID, secondId: UUID): Game {
            val game = Game()
            val event = GameScheduled(leagueId, firstId, secondId, dateTime.toEpochSecond(ZoneOffset.UTC))
            game.add(event)
            return game
        }

        fun from(events: List<Event<Game>>): Game {
            val game = Game()
            events.forEach { event -> event.apply(game) }
            game.version = events.last().version
            return game
        }
    }

    private fun add(event: Event<Game>) {
        pendingEvents.add(event)
        version = event.version
        event.apply(this)
    }

    internal fun apply(event: GamePlayed) {
        id = event.aggregateId
        leagueId = event.leagueId
        dateTime = LocalDateTime.ofEpochSecond(event.dateTime, 0, ZoneOffset.UTC)
        playerIds = Pair(event.firstId, event.secondId)
        result = Pair(
            Result(event.firstScore, event.firstDeviationDelta, event.firstRatingDelta),
            Result(event.secondScore, event.secondDeviationDelta, event.secondRatingDelta)
        )
    }

    internal fun apply(event: GameScheduled) {
        id = event.aggregateId
        leagueId = event.leagueId
        dateTime = LocalDateTime.ofEpochSecond(event.dateTime, 0, ZoneOffset.UTC)
        playerIds = Pair(event.firstId, event.secondId)
    }
}