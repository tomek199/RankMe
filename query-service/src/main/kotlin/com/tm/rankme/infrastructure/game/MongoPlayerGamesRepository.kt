package com.tm.rankme.infrastructure.game

import com.tm.rankme.infrastructure.decode
import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.PlayerGamesRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class MongoPlayerGamesRepository(
    private val accessor: MongoGameAccessor
) : MongoGamesPage(), PlayerGamesRepository {

    override fun byPlayerId(playerId: String, first: Int): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByPlayerIdOrderByTimestampDesc(playerId, pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, false, page.hasNext())
    }

    override fun byPlayerIdAfter(playerId: String, first: Int, after: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(playerId, decode(after), pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, true, page.hasNext())
    }

    override fun byPlayerIdBefore(playerId: String, first: Int, before: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByPlayerIdAndTimestampGreaterThanOrderByTimestampAsc(playerId, decode(before), pageable)
        val games = page.content.reversed().map(this::itemForEntity)
        return Page(games, page.hasNext(), true)
    }

    override fun completedByPlayerId(playerId: String, first: Int): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByPlayerIdAndResultNotNullOrderByTimestampDesc(playerId, pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, false, page.hasNext())
    }

    override fun completedByPlayerIdAfter(playerId: String, first: Int, after: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByPlayerIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(playerId, decode(after), pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, true, page.hasNext())
    }

    override fun completedByPlayerIdBefore(playerId: String, first: Int, before: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByPlayerIdAndTimestampGreaterThanAndResultNotNullOrderByTimestampAsc(playerId, decode(before), pageable)
        val games = page.content.reversed().map(this::itemForEntity)
        return Page(games, page.hasNext(), true)
    }

    override fun scheduledByPlayerId(playerId: String, first: Int): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByPlayerIdAndResultNullOrderByTimestampDesc(playerId, pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, false, page.hasNext())
    }

    override fun scheduledByPlayerIdAfter(playerId: String, first: Int, after: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByPlayerIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(playerId, decode(after), pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, true, page.hasNext())
    }

    override fun scheduledByPlayerIdBefore(playerId: String, first: Int, before: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByPlayerIdAndTimestampGreaterThanAndResultNullOrderByTimestampAsc(playerId, decode(before), pageable)
        val games = page.content.reversed().map(this::itemForEntity)
        return Page(games, page.hasNext(), true)
    }
}