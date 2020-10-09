package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import java.time.LocalDateTime

class GameFactory private constructor() {
    companion object {
        fun scheduled(competitorOne: Competitor, competitorTwo: Competitor, leagueId: String, dateTime: LocalDateTime): Game {
            val playerOne = initPlayer(competitorOne)
            val playerTwo = initPlayer(competitorTwo)
            return Game(playerOne, playerTwo, leagueId, dateTime)
        }

        fun completed(
            competitorOne: Competitor, scoreOne: Int,
            competitorTwo: Competitor, scoreTwo: Int,
            leagueId: String
        ): Game {
            val glicko = initGlicko(competitorOne, scoreOne, competitorTwo, scoreTwo)
            val playerOne = initPlayer(competitorOne, glicko.playerOneDeviation, glicko.playerOneRating, scoreOne)
            val playerTwo = initPlayer(competitorTwo, glicko.playerTwoDeviation, glicko.playerTwoRating, scoreTwo)
            return Game(playerOne, playerTwo, leagueId, LocalDateTime.now())
        }

        private fun initGlicko(
            competitorOne: Competitor,
            scoreOne: Int,
            competitorTwo: Competitor,
            scoreTwo: Int
        ): GlickoService {
            return GlickoService(
                deviationOne = competitorOne.deviation,
                ratingOne = competitorOne.rating,
                scoreOne = scoreOne,
                deviationTwo = competitorTwo.deviation,
                ratingTwo = competitorTwo.rating,
                scoreTwo = scoreTwo
            )
        }

        private fun initPlayer(competitor: Competitor): Player {
            val competitorId = competitor.id ?: throw IllegalStateException("Competitor id cannot be null!")
            return Player(competitorId, competitor.username, competitor.deviation, competitor.rating)
        }

        private fun initPlayer(competitor: Competitor, deviation: Int, rating: Int, score: Int): Player {
            val competitorId = competitor.id ?: throw IllegalStateException("Competitor id cannot be null!")
            val deviationDelta = deviation - competitor.deviation
            val ratingDelta = rating - competitor.rating
            val result = Result(score, deviationDelta, ratingDelta)
            return Player(competitorId, competitor.username, competitor.deviation, competitor.rating, result)
        }
    }
}