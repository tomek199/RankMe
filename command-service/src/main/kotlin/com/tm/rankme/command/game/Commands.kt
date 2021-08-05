package com.tm.rankme.command.game

import java.time.LocalDateTime

data class PlayGameCommand(
    val playerOneId: String,
    val playerTwoId: String,
    val playerOneScore: Int,
    val playerTwoScore: Int
) : com.tm.rankme.command.Command()

data class ScheduleGameCommand(
    val playerOneId: String,
    val playerTwoId: String,
    val dateTime: LocalDateTime
) : com.tm.rankme.command.Command()

data class CompleteGameCommand(
    val gameId: String,
    val playerOneScore: Int,
    val playerTwoScore: Int
) : com.tm.rankme.command.Command()