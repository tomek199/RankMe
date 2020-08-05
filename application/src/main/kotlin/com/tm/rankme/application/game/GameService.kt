package com.tm.rankme.application.game

import com.tm.rankme.domain.Side
import com.tm.rankme.domain.game.Game

interface GameService {
    fun get(gameId: String): Game
    fun create(game: Game): Game
    fun getSideForLeague(leagueId: String, last: Int, after: String? = null): Side<Game>
}