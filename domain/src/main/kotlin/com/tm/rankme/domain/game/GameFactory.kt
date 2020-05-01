package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import java.time.LocalDateTime

class GameFactory private constructor() {
    companion object {
        fun scheduledGame(
            firstCompetitor: Competitor, secondCompetitor: Competitor, leagueId: String, dateTime: LocalDateTime
        ): Game {
            val playerOne = createPlayer(firstCompetitor)
            val playerTwo = createPlayer(secondCompetitor)
            return Game(playerOne, playerTwo, leagueId, dateTime)
        }

        fun completedGame(scoreOne: Pair<Competitor, Int>, scoreTwo: Pair<Competitor, Int>, leagueId: String): Game {
            val playerOne = createPlayer(scoreOne.first)
            playerOne.score = scoreOne.second
            val playerTwo = createPlayer(scoreTwo.first)
            playerTwo.score = scoreTwo.second
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