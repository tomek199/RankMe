package com.tm.rankme.application.league

import com.tm.rankme.application.cqrs.Command

data class CreateLeagueCommand(val name: String) : Command()