package com.tm.rankme.model.game

import java.time.LocalDateTime

data class Game(
    val id: String,
    val leagueId: String,
    val dateTime: LocalDateTime,
    val playerOneId: String,
    val playerOneName: String,
    var playerOneRating: Int,
    var playerOneDeviation: Int,
    val playerTwoId: String,
    val playerTwoName: String,
    var playerTwoRating: Int,
    var playerTwoDeviation: Int,
    var result: Result? = null
)

data class Result(
    val playerOneScore: Int,
    val playerOneDeviationDelta: Int,
    val playerOneRatingDelta: Int,
    val playerTwoScore: Int,
    val playerTwoDeviationDelta: Int,
    val playerTwoRatingDelta: Int
)
