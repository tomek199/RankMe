package com.tm.rankme.application.game

import com.tm.rankme.domain.Side
import com.tm.rankme.domain.game.Game

interface GameService {
    fun get(gameId: String): GameModel
    fun create(
        leagueId: String,
        firstCompetitorId: String, firstScore: Int,
        secondCompetitorId: String, secondScore: Int
    ): GameModel

    fun complete(eventId: String, playerOneScore: Int, playerTwoScore: Int): GameModel
    fun getSideForLeague(leagueId: String, first: Int, after: String? = null): Side<Game>
}