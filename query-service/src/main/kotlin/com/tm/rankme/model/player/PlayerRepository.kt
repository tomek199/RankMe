package com.tm.rankme.model.player

interface PlayerRepository {
    fun byId(id: String): Player?
    fun store(player: Player)
    fun byLeagueId(leagueId: String): List<Player>
}