package com.tm.rankme.application.game

import java.time.LocalDateTime

data class GameModel(
    val id: String,
    val playerOne: PlayerModel,
    val playerTwo: PlayerModel,
    val dateTime: LocalDateTime
)

data class PlayerModel(
    val competitorId: String,
    val username: String,
    val rating: Int,
    val score: Int?
)

data class AddGameInput(
    val leagueId: String,
    val playerOneId: String,
    val playerOneScore: Int,
    val playerTwoId: String,
    val playerTwoScore: Int
)

data class CompleteGameInput(
    val matchId: String,
    val playerOneScore: Int,
    val playerTwoScore: Int
)
