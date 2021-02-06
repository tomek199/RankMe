package com.tm.rankme.cqrs.command.league

import com.tm.rankme.cqrs.command.Command
import java.util.*

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