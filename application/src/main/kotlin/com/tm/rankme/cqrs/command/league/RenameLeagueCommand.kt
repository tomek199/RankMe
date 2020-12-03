package com.tm.rankme.cqrs.command.league

import com.tm.rankme.cqrs.command.Command
import java.util.*

data class RenameLeagueCommand(val id: UUID, val name: String) : Command()