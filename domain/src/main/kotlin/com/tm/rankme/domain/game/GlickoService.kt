package com.tm.rankme.domain.game

import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal class GlickoService(playerOne: Player,
                             playerTwo: Player) {
    private val q = ln(10.0) / 400
    val playerOneDeviation: Int
    val playerTwoDeviation: Int
    val playerOneRating: Int
    val playerTwoRating: Int

    init {
        val playerOneScore = playerOne.score?: throw IllegalArgumentException("Player one score is not provided!")
        val playerTwoScore = playerTwo.score?: throw IllegalArgumentException("Player two Score is not provided!")
        playerOneDeviation = calculateDeviation(playerOne, playerTwo)
        playerTwoDeviation = calculateDeviation(playerTwo, playerOne)
        playerOneRating = calculateRating(playerOne, playerTwo, result(playerOneScore, playerTwoScore))
        playerTwoRating = calculateRating(playerTwo, playerOne, result(playerTwoScore, playerOneScore))
    }

    private fun calculateDeviation(player: Player, opponent: Player): Int {
        return sqrt((1.0 / (player.deviation * player.deviation) + 1.0 / dSquare(player, opponent))
                .pow(-1)).roundToInt()
    }

    private fun calculateRating(player: Player, opponent: Player, result: Float): Int {
        return (player.rating +
                q / (1.0 / (player.deviation * player.deviation) + 1.0 / dSquare(player, opponent)) *
                g(opponent) * (result - e(player, opponent))).roundToInt()
    }

    private fun g(player: Player): Double {
        return 1.0 / (sqrt(1 + 3 * (q * q) * (player.deviation * player.deviation) / (PI * PI)))
    }

    private fun e(player: Player, opponent: Player): Double {
        return 1 / (1 + 10.0.pow(-g(opponent) * (player.rating - opponent.rating) / 400))
    }

    private fun dSquare(player: Player, opponent: Player): Double {
        val estimate = e(player, opponent)
        return 1 / ((q * q) * g(opponent).pow(2) * estimate * (1 - estimate))
    }

    private fun result(playerScore: Int, opponentScore: Int): Float {
        return when {
            playerScore > opponentScore -> 1f
            playerScore < opponentScore -> 0f
            else -> 0.5f
        }
    }
}
