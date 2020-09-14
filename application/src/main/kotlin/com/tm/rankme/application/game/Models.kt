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
    val score: Int
)

// todo remove obsolete connection classes
data class GameConnection(
    val totalCount: Int,
    val edges: List<GameEdge>,
    val pageInfo: PageInfo
)

data class GameEdge(
    val node: GameModel,
    val cursor: String
)

data class PageInfo(
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean
)

data class AddGameInput(
    val leagueId: String,
    val playerOneId: String,
    val playerOneScore: Int,
    val playerTwoId: String,
    val playerTwoScore: Int
)

data class CompleteGameInput(
    val eventId: String,
    val playerOneScore: Int,
    val playerTwoScore: Int
)
