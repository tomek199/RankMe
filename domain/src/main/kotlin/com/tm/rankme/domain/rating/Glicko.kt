package com.tm.rankme.domain.rating

import com.tm.rankme.domain.competitor.Statistics
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal class Glicko(private val playerOne: Statistics,
                      private val playerTwo: Statistics,
                      private val score: Pair<Int, Int>) : Algorithm {
    private val q = ln(10.0) / 400

    override fun playerOneDeviation(): Int {
        return sqrt((1.0 / (playerOne.deviation * playerOne.deviation) + 1.0 / dSquare(playerOne, playerTwo))
                .pow(-1)).roundToInt()
    }

    override fun playerTwoDeviation(): Int {
        return sqrt((1.0 / (playerTwo.deviation * playerTwo.deviation) + 1.0 / dSquare(playerTwo, playerOne))
                .pow(-1)).roundToInt()
    }

    override fun playerOneRating(): Int {
        return (playerOne.rating +
                q / (1.0 / (playerOne.deviation * playerOne.deviation) + 1.0 / dSquare(playerOne, playerTwo)) *
                g(playerTwo) * (playerOneScore() - e(playerOne, playerTwo))).roundToInt()
    }

    override fun playerTwoRating(): Int {
        return (playerTwo.rating +
                q / (1.0 / (playerTwo.deviation * playerTwo.deviation) + 1.0 / dSquare(playerTwo, playerOne)) *
                g(playerOne) * (playerTwoScore() - e(playerTwo, playerOne))).roundToInt()
    }

    private fun g(player: Statistics): Double {
        return 1.0 / (sqrt(1 + 3 * (q * q) * (player.deviation * player.deviation) / (PI * PI)))
    }

    private fun e(player: Statistics, opponent: Statistics): Double {
        return 1 / (1 + 10.0.pow(-g(opponent) * (player.rating - opponent.rating) / 400))
    }

    private fun dSquare(player: Statistics, opponent: Statistics): Double {
        val estimate = e(player, opponent)
        return 1 / ((q * q) * g(opponent).pow(2) * estimate * (1 - estimate))
    }

    private fun playerOneScore(): Float {
        return when {
            score.first > score.second -> 1f
            score.first < score.second -> 0f
            else -> 0.5f
        }
    }

    private fun playerTwoScore(): Float {
        return when {
            score.first < score.second -> 1f
            score.first > score.second -> 0f
            else -> 0.5f
        }
    }
}