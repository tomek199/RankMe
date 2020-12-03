package com.tm.rankme.cqrs.command.league

import com.tm.rankme.cqrs.command.Command

data class CreateLeagueCommand(val name: String) : Command()