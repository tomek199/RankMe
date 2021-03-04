package com.tm.rankme.command.game

import com.tm.rankme.command.Command
import java.time.LocalDateTime
import java.util.*

data class PlayGameCommand(
    val playerOneId: UUID,
    val playerTwoId: UUID,
    val playerOneScore: Int,
    val playerTwoScore: Int
) : com.tm.rankme.command.Command()

data class ScheduleGameCommand(
    val playerOneId: UUID,
    val playerTwoId: UUID,
    val dateTime: LocalDateTime
) : com.tm.rankme.command.Command()

data class CompleteGameCommand(
    val gameId: UUID,
    val playerOneScore: Int,
    val playerTwoScore: Int
) : com.tm.rankme.command.Command()