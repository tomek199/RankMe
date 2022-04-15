package com.tm.rankme.infrastructure.game

import com.tm.rankme.infrastructure.decode
import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.PlayerGamesRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Page as JdbcPage

@Repository
class MongoPlayerGamesRepository(
    private val accessor: MongoGameAccessor
) : MongoGamesPage(), PlayerGamesRepository {

    override fun byPlayerId(playerId: String, first: Int, after: String?): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page =
            if (after == null) accessor.getByPlayerIdOrderByTimestampDesc(playerId, pageable)
            else accessor.getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(playerId, decode(after), pageable)
        return Page(pageItems(page), after != null, page.hasNext())
    }

    override fun completedByPlayerId(playerId: String, first: Int, after: String?): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page =
            if (after == null) accessor.getByPlayerIdAndResultNotNullOrderByTimestampDesc(playerId, pageable)
            else accessor.getByPlayerIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(playerId, decode(after), pageable)
        return Page(pageItems(page), after != null, page.hasNext())
    }

    override fun scheduledByPlayerId(playerId: String, first: Int, after: String?): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page =
            if (after == null) accessor.getByPlayerIdAndResultNullOrderByTimestampDesc(playerId, pageable)
            else accessor.getByPlayerIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(playerId, decode(after), pageable)
        return Page(pageItems(page), after != null, page.hasNext())
    }

    private fun pageItems(page: JdbcPage<GameEntity>) = page.content.map(this::itemForEntity)
}