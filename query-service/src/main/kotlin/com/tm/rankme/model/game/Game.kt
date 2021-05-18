package com.tm.rankme.model.game

import java.time.LocalDateTime
import java.util.*

data class Game(
    val id: UUID,
    val leagueId: UUID,
    val dateTime: LocalDateTime,
    val playerOneId: UUID,
    val playerOneName: String,
    val playerOneRating: Int,
    val playerOneDeviation: Int,
    val playerTwoId: UUID,
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
