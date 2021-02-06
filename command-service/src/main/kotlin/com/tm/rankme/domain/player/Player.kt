package com.tm.rankme.domain.player

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Player private constructor() : AggregateRoot() {
    val pendingEvents = mutableListOf<Event<Player>>()
    lateinit var leagueId: UUID
        private set
    lateinit var name: String
        private set
    var deviation: Int = 350
        private set
    var rating: Int = 1500
        private set
    var lastGame: LocalDate? = null

    companion object {
        fun create(leagueId: UUID, name: String): Player {
            val event = PlayerCreated(leagueId, name)
            val player = Player()
            player.add(event)
            return player
        }

        fun from(events: List<Event<Player>>): Player {
            val player = Player()
            events.forEach { event -> event.apply(player) }
            player.version = events.last().version
            return player
        }
    }

    fun playedWith(opponent: Player, playerScore: Int, opponentScore: Int): GameResult {
        val glicko = GlickoService(
            this.newDeviation(), this.rating, playerScore,
            opponent.newDeviation(), opponent.rating, opponentScore
        )
        val playerEvent = PlayerPlayedGame(
            glicko.playerOneDeviation - this.deviation, glicko.playerOneRating - this.rating,
            playerScore, this.id, ++this.version
        )
        this.add(playerEvent)
        val opponentEvent = PlayerPlayedGame(
            glicko.playerTwoDeviation - opponent.deviation, glicko.playerTwoRating - opponent.rating,
            opponentScore, opponent.id, ++opponent.version
        )
        opponent.add(opponentEvent)
        return GameResult(playerEvent, opponentEvent)
    }

    private fun newDeviation(): Int {
        if (lastGame == null) return deviation
        val c = 48
        val weeks = ChronoUnit.WEEKS.between(lastGame, LocalDate.now()) + 1
        val newDeviation = sqrt(((deviation * deviation) + (c * c) * weeks).toDouble()).roundToInt()
        return min(350, newDeviation)
    }

    private fun add(event: Event<Player>) {
        pendingEvents.add(event)
        version = event.version
        event.apply(this)
    }

    internal fun apply(event: PlayerCreated) {
        id = event.aggregateId
        leagueId = event.leagueId
        name = event.name
        deviation = event.deviation
        rating = event.rating
    }

    internal fun apply(event: PlayerPlayedGame) {
        deviation += event.deviationDelta
        rating += event.ratingDelta
        lastGame = Instant.ofEpochMilli(event.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    }
}