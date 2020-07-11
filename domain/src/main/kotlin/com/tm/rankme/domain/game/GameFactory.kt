package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import java.time.LocalDateTime

class GameFactory private constructor() {
    companion object {
        fun create(
            competitorOne: Competitor, scoreOne: Int,
            competitorTwo: Competitor, scoreTwo: Int,
            leagueId: String
        ): Game {
            val playerOne = createPlayer(competitorOne)
            playerOne.score = scoreOne
            val playerTwo = createPlayer(competitorTwo)
            playerTwo.score = scoreTwo
            val glicko = GlickoService(playerOne, playerTwo)
            playerOne.update(glicko.playerOneDeviation, glicko.playerOneRating)
            playerTwo.update(glicko.playerTwoDeviation, glicko.playerTwoRating)
            return Game(playerOne, playerTwo, leagueId, LocalDateTime.now())
        }

        private fun createPlayer(competitor: Competitor): Player {
            return competitor.id?.let {
                Player(it, competitor.username, competitor.statistics.deviation, competitor.statistics.rating)
            } ?: throw IllegalStateException("Competitor ids cannot be null!")
        }
    }
}