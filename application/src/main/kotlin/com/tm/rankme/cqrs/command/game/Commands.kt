package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.Command
import java.time.LocalDateTime
import java.util.*

data class PlayGameCommand(
    val playerOneId: UUID,
    val playerTwoId: UUID,
    val playerOneScore: Int,
    val playerTwoScore: Int
) : Command()

data class ScheduleGameCommand(
    val playerOneId: UUID,
    val playerTwoId: UUID,
    val dateTime: LocalDateTime
) : Command()

data class CompleteGameCommand(
    val gameId: UUID,
    val playerOneScore: Int,
    val playerTwoScore: Int
) : Command()