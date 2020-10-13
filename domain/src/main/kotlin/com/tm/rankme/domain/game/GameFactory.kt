package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import java.time.LocalDateTime

class GameFactory private constructor() {
    companion object {
        fun scheduled(competitorOne: Competitor, competitorTwo: Competitor, dateTime: LocalDateTime): Game {
            val leagueId = getLeagueId(competitorOne.leagueId, competitorTwo.leagueId)
            val playerOne = initPlayer(competitorOne)
            val playerTwo = initPlayer(competitorTwo)
            return Game(playerOne, playerTwo, leagueId, dateTime)
        }

        fun completed(competitorOne: Competitor, scoreOne: Int, competitorTwo: Competitor, scoreTwo: Int): Game {
            val leagueId = getLeagueId(competitorOne.leagueId, competitorTwo.leagueId)
            val glicko = GlickoService(
                deviationOne = competitorOne.deviation, ratingOne = competitorOne.rating, scoreOne = scoreOne,
                deviationTwo = competitorTwo.deviation, ratingTwo = competitorTwo.rating, scoreTwo = scoreTwo
            )
            val playerOne = initPlayer(competitorOne, glicko.playerOneDeviation, glicko.playerOneRating, scoreOne)
            val playerTwo = initPlayer(competitorTwo, glicko.playerTwoDeviation, glicko.playerTwoRating, scoreTwo)
            return Game(playerOne, playerTwo, leagueId, LocalDateTime.now(), Type.COMPLETED)
        }

        private fun initPlayer(competitor: Competitor): Player {
            return Player(getCompetitorId(competitor), competitor.username, competitor.deviation, competitor.rating)
        }

        private fun initPlayer(competitor: Competitor, deviation: Int, rating: Int, score: Int): Player {
            val deviationDelta = deviation - competitor.deviation
            val ratingDelta = rating - competitor.rating
            val result = Result(score, deviationDelta, ratingDelta)
            return Player(getCompetitorId(competitor), competitor.username, competitor.deviation, competitor.rating, result)
        }

        private fun getCompetitorId(competitor: Competitor): String {
            return competitor.id ?: throw IllegalStateException("Competitor id cannot be null!")
        }

        private fun getLeagueId(leagueIdOne: String, leagueIdTwo: String): String {
            if (leagueIdOne != leagueIdTwo) throw IllegalStateException("Competitors do not belong to the same league!")
            return leagueIdOne
        }
    }
}