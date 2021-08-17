package com.tm.rankme.command.player

data class CreatePlayerCommand(
    val leagueId: String,
    val name: String
) : com.tm.rankme.command.Command()