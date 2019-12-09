package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import java.time.LocalDate

class Game(first: Competitor, second: Competitor, var date: LocalDate) {
    val id: String? = null
    val playerOne: Player = first.id?.let {
            Player(it, first.username, first.statistics.deviation, first.statistics.rating)
        } ?: throw IllegalStateException("Competitor ids cannot be null!")
    val playerTwo: Player = second.id?.let {
            Player(it, second.username, second.statistics.deviation, second.statistics.rating)
        } ?: throw IllegalStateException("Competitor ids cannot be null!")

    fun complete(scoreOne: Pair<Statistics, Int>, scoreTwo: Pair<Statistics, Int>, gameDate: LocalDate) {
        date = gameDate
        val glicko = GlickoService(scoreOne, scoreTwo)

        playerOne.score = scoreOne.second
        playerOne.deviation = glicko.playerOneDeviation()
        val playerOneNewRating = glicko.playerOneRating()
        playerOne.rating = playerOneNewRating
        playerOne.ratingDelta = playerOneNewRating - scoreOne.first.rating

        playerTwo.score = scoreTwo.second
        playerTwo.deviation = glicko.playerTwoDeviation()
        val playerTwoNewRating = glicko.playerTwoRating()
        playerTwo.rating = playerTwoNewRating
        playerTwo.ratingDelta = playerTwoNewRating - scoreTwo.first.rating
    }
}
