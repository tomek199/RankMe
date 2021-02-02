package com.tm.rankme.api.mutation

import java.time.LocalDateTime
import java.util.*

open class Command

data class ChangeLeagueSettingsCommand(
    val id: UUID,
    val allowDraws: Boolean,
    val maxScore: Int
) : Command()

data class CreateLeagueCommand(
    val name: String
) : Command()

data class RenameLeagueCommand(
    val id: UUID,
    val name: String
) : Command()

data class CreatePlayerCommand(
    val leagueId: UUID,
    val name: String
) : Command()

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