package com.tm.rankme.application.league

import com.tm.rankme.application.cqrs.Command
import java.util.*

data class RenameLeagueCommand(val id: UUID, val name: String) : Command()