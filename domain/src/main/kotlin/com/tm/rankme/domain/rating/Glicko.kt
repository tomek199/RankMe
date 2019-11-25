package com.tm.rankme.domain.rating

import com.tm.rankme.domain.player.LeagueStats
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal class Glicko(private val playerOne: LeagueStats,
                      private val playerTwo: LeagueStats, private val score: Pair<Int, Int>) : Algorithm {
    private val q = ln(10.0) / 400
    private val c = 48
    private val gPlayerOne = 1 / (sqrt(1 + 3 * (q * q) * (playerOne.ratingDeviation * playerOne.ratingDeviation) / (PI * PI)))
    private val gPlayerTwo = 1 / (sqrt(1 + 3 * (q * q) * (playerTwo.ratingDeviation * playerTwo.ratingDeviation) / (PI * PI)))
    private val ePlayerOne = 1 / (1 + 10.0.pow(-gPlayerTwo * (playerOne.rating - playerTwo.rating) / (-400)))
    private val ePlayerTwo = 1 / (1 + 10.0.pow(-gPlayerOne * (playerTwo.rating - playerOne.rating) / (-400)))
    private val dSquarePlayerOne = 1 / ((q * q) * (gPlayerTwo * gPlayerTwo) * ePlayerOne * (1 - ePlayerOne))
    private val dSquarePlayerTwo = 1 / ((q * q) * (gPlayerOne * gPlayerOne) * ePlayerTwo * (1 - ePlayerTwo))

    override fun playerOneRating(): Int {
        return (playerOne.rating +
                q / (1.0 / (playerOne.ratingDeviation * playerOne.ratingDeviation) + 1 / dSquarePlayerOne) *
                gPlayerTwo * (playerOneScore() - ePlayerOne)).roundToInt()
    }

    override fun playerTwoRating(): Int {
        return (playerTwo.rating +
                q / (1.0 / (playerTwo.ratingDeviation * playerTwo.ratingDeviation) + 1 / dSquarePlayerTwo) *
                gPlayerOne * (playerTwoScore() - ePlayerTwo)).roundToInt()
    }

    override fun playerOneDeviation(): Int {
        return sqrt((1.0 / (playerOne.ratingDeviation * playerOne.ratingDeviation) + 1.0 / dSquarePlayerOne).pow(-1))
                .roundToInt()
    }

    override fun playerTwoDeviation(): Int {
        return sqrt((1.0 / (playerTwo.ratingDeviation * playerTwo.ratingDeviation) + 1.0 / dSquarePlayerTwo).pow(-1))
                .roundToInt()
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