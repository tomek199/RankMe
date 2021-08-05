package com.tm.rankme.domain.player

import kotlin.math.*

internal class GlickoService(
    deviationOne: Int, ratingOne: Int, scoreOne: Int,
    deviationTwo: Int, ratingTwo: Int, scoreTwo: Int
) {
    private val q = ln(10.0) / 400
    val playerOneDeviation: Int
    val playerTwoDeviation: Int
    val playerOneRating: Int
    val playerTwoRating: Int

    init {
        playerOneDeviation = calculateDeviation(deviationOne, ratingOne, deviationTwo, ratingTwo)
        playerTwoDeviation = calculateDeviation(deviationTwo, ratingTwo, deviationOne, ratingOne)
        playerOneRating = calculateRating(deviationOne, ratingOne, deviationTwo, ratingTwo, result(scoreOne, scoreTwo))
        playerTwoRating = calculateRating(deviationTwo, ratingTwo, deviationOne, ratingOne, result(scoreTwo, scoreOne))
    }

    private fun calculateDeviation(
        playerDeviation: Int, playerRating: Int,
        opponentDeviation: Int, opponentRating: Int
    ): Int {
        return sqrt(
            (1.0 / (playerDeviation * playerDeviation) + 1.0 / dSquare(playerRating, opponentDeviation, opponentRating))
                .pow(-1)
        ).roundToInt()
    }

    private fun calculateRating(
        playerDeviation: Int, playerRating: Int,
        opponentDeviation: Int, opponentRating: Int,
        result: Float
    ): Int {
        return (playerRating +
            q / (1.0 / (playerDeviation * playerDeviation) + 1.0 /
            dSquare(playerRating, opponentDeviation, opponentRating)) *
            g(opponentDeviation) * (result - e(playerRating, opponentDeviation, opponentRating))).roundToInt()
    }

    private fun e(playerRating: Int, opponentDeviation: Int, opponentRating: Int): Double {
        return 1 / (1 + 10.0.pow(-g(opponentDeviation) * (playerRating - opponentRating) / 400))
    }

    private fun dSquare(playerRating: Int, opponentDeviation: Int, opponentRating: Int): Double {
        val estimate = e(playerRating, opponentDeviation, opponentRating)
        return 1 / ((q * q) * g(opponentDeviation).pow(2) * estimate * (1 - estimate))
    }

    private fun g(playerDeviation: Int): Double {
        return 1.0 / (sqrt(1 + 3 * (q * q) * (playerDeviation * playerDeviation) / (PI * PI)))
    }

    private fun result(playerScore: Int, opponentScore: Int): Float {
        return when {
            playerScore > opponentScore -> 1f
            playerScore < opponentScore -> 0f
            else -> 0.5f
        }
    }
}