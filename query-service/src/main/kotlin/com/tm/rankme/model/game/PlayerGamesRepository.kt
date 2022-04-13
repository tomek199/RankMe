package com.tm.rankme.model.game

import com.tm.rankme.model.Page

interface PlayerGamesRepository {
    fun byPlayerId(playerId: String, first: Int, after: String? = null): Page<Game>
    fun completedByPlayerId(playerId: String, first: Int, after: String? = null): Page<Game>
    fun scheduledByPlayerId(playerId: String, first: Int, after: String? = null): Page<Game>
}