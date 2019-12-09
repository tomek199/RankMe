package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Statistics
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal class GlickoService(scoreOne: Pair<Statistics, Int>,
                             scoreTwo: Pair<Statistics, Int>) {

    private val playerOne: Statistics = scoreOne.first
    private val playerTwo: Statistics = scoreTwo.first
    private val playerOneResult = calculateResult(scoreOne, scoreTwo)
    private val playerTwoResult = calculateResult(scoreTwo, scoreOne)
    private val q = ln(10.0) / 400

    private fun calculateResult(player: Pair<Statistics, Int>, opponent: Pair<Statistics, Int>): Float {
        return when {
            player.second > opponent.second -> 1f
            player.second < opponent.second -> 0f
            else -> 0.5f
        }
    }

    fun playerOneDeviation(): Int {
        return sqrt((1.0 / (playerOne.deviation * playerOne.deviation) + 1.0 / dSquare(playerOne, playerTwo))
                .pow(-1)).roundToInt()
    }

    fun playerTwoDeviation(): Int {
        return sqrt((1.0 / (playerTwo.deviation * playerTwo.deviation) + 1.0 / dSquare(playerTwo, playerOne))
                .pow(-1)).roundToInt()
    }

    fun playerOneRating(): Int {
        return (playerOne.rating +
                q / (1.0 / (playerOne.deviation * playerOne.deviation) + 1.0 / dSquare(playerOne, playerTwo)) *
                g(playerTwo) * (playerOneResult - e(playerOne, playerTwo))).roundToInt()
    }

    fun playerTwoRating(): Int {
        return (playerTwo.rating +
                q / (1.0 / (playerTwo.deviation * playerTwo.deviation) + 1.0 / dSquare(playerTwo, playerOne)) *
                g(playerOne) * (playerTwoResult - e(playerTwo, playerOne))).roundToInt()
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
}