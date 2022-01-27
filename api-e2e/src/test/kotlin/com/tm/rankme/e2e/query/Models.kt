package com.tm.rankme.e2e.query

data class Player(
    val id: String,
    val name: String,
    val deviation: Int,
    val rating: Int,
    val games: Connection<Game>?
)

data class League(
    val id: String,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int,
    val players: List<Player>?,
    val games: Connection<Game>?
)

data class Game(
    val id: String,
    val dateTime: String,
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

data class CompletedGame(
    val id: String,
    val dateTime: String,
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

data class ScheduledGame(
    val id: String,
    val dateTime: String,
    val playerOneId: String,
    val playerOneName: String,
    val playerOneRating: Int,
    val playerOneDeviation: Int,
    val playerTwoId: String,
    val playerTwoName: String,
    val playerTwoRating: Int,
    val playerTwoDeviation: Int,
)

data class Connection<T>(
    val pageInfo: PageInfo,
    val edges: List<Edge<T>>
)

data class PageInfo(
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean,
    val startCursor: String?,
    val endCursor: String?
)

data class Edge<T>(
    val cursor: String,
    val node: T
)