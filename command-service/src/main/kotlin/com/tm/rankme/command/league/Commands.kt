package com.tm.rankme.command.league

import com.tm.rankme.command.Command
import java.util.*

data class ChangeLeagueSettingsCommand(
    val id: UUID,
    val allowDraws: Boolean,
    val maxScore: Int
) : com.tm.rankme.command.Command()

data class CreateLeagueCommand(
    val name: String
) : com.tm.rankme.command.Command()

data class RenameLeagueCommand(
    val id: UUID,
    val name: String
) : com.tm.rankme.command.Command()