package com.tm.rankme.model.game

import com.tm.rankme.model.Page

interface GameRepository {
    fun byId(id: String): Game?
    fun store(game: Game)
    fun byLeagueId(leagueId: String, first: Int, after: String? = null): Page<Game>
    fun byPlayerId(playerId: String, first: Int, after: String? = null): Page<Game>
}