package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import java.time.LocalDateTime

class Game(first: Competitor, second: Competitor, var dateTime: LocalDateTime) {
    val id: String? = null
    val playerOne: Player = first.id?.let {
            Player(it, first.username, first.statistics.deviation, first.statistics.rating)
        } ?: throw IllegalStateException("Competitor ids cannot be null!")
    val playerTwo: Player = second.id?.let {
            Player(it, second.username, second.statistics.deviation, second.statistics.rating)
        } ?: throw IllegalStateException("Competitor ids cannot be null!")

    constructor(scoreOne: Pair<Competitor, Int>, scoreTwo: Pair<Competitor, Int>)
            : this(scoreOne.first, scoreTwo.first, LocalDateTime.now()) {
        playerOne.score = scoreOne.second
        playerTwo.score = scoreTwo.second
        recalculatePlayers()
    }

    fun complete(scoreOne: Pair<Competitor, Int>, scoreTwo: Pair<Competitor, Int>) {
        dateTime = LocalDateTime.now()
        updatePlayers(scoreOne, scoreTwo)
        recalculatePlayers()
    }

    private fun updatePlayers(scoreOne: Pair<Competitor, Int>, scoreTwo: Pair<Competitor, Int>) {
        playerOne.score = scoreOne.second
        playerOne.deviation = scoreOne.first.statistics.deviation
        playerOne.rating = scoreOne.first.statistics.rating
        playerTwo.score = scoreTwo.second
        playerTwo.deviation = scoreTwo.first.statistics.deviation
        playerTwo.rating = scoreTwo.first.statistics.rating
    }

    private fun recalculatePlayers() {
        val glicko = GlickoService(playerOne, playerTwo)
        playerOne.update(glicko.playerOneDeviation, glicko.playerOneRating)
        playerTwo.update(glicko.playerTwoDeviation, glicko.playerTwoRating)
    }
}
