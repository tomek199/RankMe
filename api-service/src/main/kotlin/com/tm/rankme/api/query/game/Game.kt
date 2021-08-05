package com.tm.rankme.api.query.game

import java.time.LocalDateTime

data class Game(
    val id: String,
    val dateTime: LocalDateTime,
    val playerOneId: String,
    val playerOneName: String,
    val playerOneRating: Int,
    val playerOneDeviation: Int,
    val playerTwoId: String,
    val playerTwoName: String,
    val playerTwoRating: Int,
    val playerTwoDeviation: Int,
    val result: Result? = null
)

data class Result(
    val playerOneScore: Int,
    val playerOneDeviationDelta: Int,
    val playerOneRatingDelta: Int,
    val playerTwoScore: Int,
    val playerTwoDeviationDelta: Int,
    val playerTwoRatingDelta: Int
)