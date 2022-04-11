package com.tm.rankme.model.game

import com.tm.rankme.model.Page

interface GameRepository {
    fun byId(id: String): Game?
    fun store(game: Game)
    fun byLeagueId(leagueId: String, first: Int): Page<Game>
    fun byLeagueIdAfter(leagueId: String, first: Int, after: String): Page<Game>
    fun byLeagueIdBefore(leagueId: String, first: Int, before: String): Page<Game>
    fun completedByLeagueId(leagueId: String, first: Int): Page<Game>
    fun completedByLeagueIdAfter(leagueId: String, first: Int, after: String): Page<Game>
    fun completedByLeagueIdBefore(leagueId: String, first: Int, before: String): Page<Game>
    fun scheduledByLeagueId(leagueId: String, first: Int): Page<Game>
    fun scheduledByLeagueIdAfter(leagueId: String, first: Int, after: String): Page<Game>
    fun scheduledByLeagueIdBefore(leagueId: String, first: Int, before: String): Page<Game>
    fun byPlayerId(playerId: String, first: Int, after: String? = null): Page<Game>
    fun completedByPlayerId(playerId: String, first: Int, after: String? = null): Page<Game>
    fun scheduledByPlayerId(playerId: String, first: Int, after: String? = null): Page<Game>
}