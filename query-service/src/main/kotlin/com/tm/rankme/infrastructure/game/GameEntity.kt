package com.tm.rankme.infrastructure.game

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "game")
data class GameEntity(
    @Id val id: String,
    val leagueId: String,
    val dateTime: LocalDateTime,
    val playerOneId: String,
    val playerOneName: String,
    val playerOneRating: Int,
    val playerOneDeviation: Int,
    val playerTwoId: String,
    val playerTwoName: String,
    val playerTwoRating: Int,
    val playerTwoDeviation: Int,
    val result: Result? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class Result(
    val playerOneScore: Int,
    val playerOneDeviationDelta: Int,
    val playerOneRatingDelta: Int,
    val playerTwoScore: Int,
    val playerTwoDeviationDelta: Int,
    val playerTwoRatingDelta: Int
)