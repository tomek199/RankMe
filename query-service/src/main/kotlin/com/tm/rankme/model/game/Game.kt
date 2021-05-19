package com.tm.rankme.model.game

import java.time.LocalDateTime
import java.util.*

data class Game(
    val id: UUID,
    val leagueId: UUID,
    val dateTime: LocalDateTime,
    val playerOneId: UUID,
    val playerOneName: String,
    var playerOneRating: Int,
    var playerOneDeviation: Int,
    val playerTwoId: UUID,
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
