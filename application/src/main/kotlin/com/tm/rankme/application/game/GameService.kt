package com.tm.rankme.application.game

import com.tm.rankme.domain.Side
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game

interface GameService {
    fun get(gameId: String): Game
    fun create(
        leagueId: String,
        firstCompetitor: Competitor, firstScore: Int,
        secondCompetitor: Competitor, secondScore: Int
    ): Game

    fun getSideForLeague(leagueId: String, first: Int, after: String? = null): Side<Game>
}