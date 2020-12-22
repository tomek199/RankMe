package com.tm.rankme.cqrs.command.player

import com.tm.rankme.cqrs.command.Command
import java.util.*

data class CreatePlayerCommand(
    val leagueId: UUID,
    val name: String
) : Command()