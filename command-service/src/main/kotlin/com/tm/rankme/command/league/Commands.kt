package com.tm.rankme.command.league

data class ChangeLeagueSettingsCommand(
    val id: String,
    val allowDraws: Boolean,
    val maxScore: Int
) : com.tm.rankme.command.Command()

data class CreateLeagueCommand(
    val name: String
) : com.tm.rankme.command.Command()

data class RenameLeagueCommand(
    val id: String,
    val name: String
) : com.tm.rankme.command.Command()