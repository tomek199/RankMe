package com.tm.rankme.model.game

import com.tm.rankme.model.Page

interface PlayerGamesRepository {
    fun byPlayerId(playerId: String, first: Int): Page<Game>
    fun byPlayerIdAfter(playerId: String, first: Int, after: String): Page<Game>
    fun byPlayerIdBefore(playerId: String, first: Int, before: String): Page<Game>
    fun completedByPlayerId(playerId: String, first: Int): Page<Game>
    fun completedByPlayerIdAfter(playerId: String, first: Int, after: String): Page<Game>
    fun completedByPlayerIdBefore(playerId: String, first: Int, before: String): Page<Game>
    fun scheduledByPlayerId(playerId: String, first: Int): Page<Game>
    fun scheduledByPlayerIdAfter(playerId: String, first: Int, after: String): Page<Game>
    fun scheduledByPlayerIdBefore(playerId: String, first: Int, before: String): Page<Game>
}