package com.tm.rankme.mutation

import java.time.LocalDateTime

open class Command

data class ChangeLeagueSettingsCommand(
    val id: String,
    val allowDraws: Boolean,
    val maxScore: Int
) : Command()

data class CreateLeagueCommand(
    val name: String
) : Command()

data class RenameLeagueCommand(
    val id: String,
    val name: String
) : Command()

data class CreatePlayerCommand(
    val leagueId: String,
    val name: String
) : Command()

data class PlayGameCommand(
    val playerOneId: String,
    val playerTwoId: String,
    val playerOneScore: Int,
    val playerTwoScore: Int
) : Command()

data class ScheduleGameCommand(
    val playerOneId: String,
    val playerTwoId: String,
    val dateTime: LocalDateTime
) : Command()

data class CompleteGameCommand(
    val gameId: String,
    val playerOneScore: Int,
    val playerTwoScore: Int
) : Command()