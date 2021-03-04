package com.tm.rankme.command.player

import com.tm.rankme.command.Command
import java.util.*

data class CreatePlayerCommand(
    val leagueId: UUID,
    val name: String
) : com.tm.rankme.command.Command()