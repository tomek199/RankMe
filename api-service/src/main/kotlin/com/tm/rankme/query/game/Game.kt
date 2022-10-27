package com.tm.rankme.query.game

import java.time.LocalDateTime

interface Game {
    val id: String
    val dateTime: LocalDateTime
    val playerOneId: String
    val playerOneName: String
    val playerOneRating: Int
    val playerOneDeviation: Int
    val playerTwoId: String
    val playerTwoName: String
    val playerTwoRating: Int
    val playerTwoDeviation: Int
}

class CompletedGame(
    override val id: String, override val dateTime: LocalDateTime,
    override val playerOneId: String, override val playerOneName: String,
    override val playerOneRating: Int, override val playerOneDeviation: Int,
    override val playerTwoId: String, override val playerTwoName: String,
    override val playerTwoRating: Int, override val playerTwoDeviation: Int,
    val result: Result
) : Game

class ScheduledGame(
    override val id: String, override val dateTime: LocalDateTime,
    override val playerOneId: String, override val playerOneName: String,
    override val playerOneRating: Int, override val playerOneDeviation: Int,
    override val playerTwoId: String, override val playerTwoName: String,
    override val playerTwoRating: Int, override val playerTwoDeviation: Int
) : Game

data class Result(
    val playerOneScore: Int,
    val playerOneDeviationDelta: Int,
    val playerOneRatingDelta: Int,
    val playerTwoScore: Int,
    val playerTwoDeviationDelta: Int,
    val playerTwoRatingDelta: Int
)