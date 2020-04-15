package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import java.time.LocalDateTime

class Game internal constructor(
    val playerOne: Player, val playerTwo: Player,
    val leagueId: String, var dateTime: LocalDateTime
) {

    var id: String? = null
        private set

    constructor(id: String, playerOne: Player, playerTwo: Player, leagueId: String, dateTime: LocalDateTime)
        : this(playerOne, playerTwo, leagueId, dateTime) {
        this.id = id
    }

    fun complete(scoreOne: Pair<Competitor, Int>, scoreTwo: Pair<Competitor, Int>) {
        dateTime = LocalDateTime.now()
        updatePlayers(scoreOne, scoreTwo)
        recalculatePlayersStats()
    }

    private fun updatePlayers(scoreOne: Pair<Competitor, Int>, scoreTwo: Pair<Competitor, Int>) {
        playerOne.score = scoreOne.second
        playerOne.deviation = scoreOne.first.statistics.deviation
        playerOne.rating = scoreOne.first.statistics.rating
        playerTwo.score = scoreTwo.second
        playerTwo.deviation = scoreTwo.first.statistics.deviation
        playerTwo.rating = scoreTwo.first.statistics.rating
    }

    private fun recalculatePlayersStats() {
        val glicko = GlickoService(playerOne, playerTwo)
        playerOne.update(glicko.playerOneDeviation, glicko.playerOneRating)
        playerTwo.update(glicko.playerTwoDeviation, glicko.playerTwoRating)
    }
}
