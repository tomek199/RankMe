package com.tm.rankme.infrastructure.game

import com.tm.rankme.model.Item
import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.PlayerGamesRepository
import com.tm.rankme.model.game.Result
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.util.*
import org.springframework.data.domain.Page as JdbcPage

@Repository
class MongoPlayerGamesRepository(
    private val accessor: MongoGameAccessor
) : PlayerGamesRepository {

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

    private fun itemForEntity(entity: GameEntity) = Item(gameFromEntity(entity), encode(entity.timestamp))

    private fun gameFromEntity(entity: GameEntity): Game {
        val result = entity.result?.let { entityResult -> Result(
            entityResult.playerOneScore, entityResult.playerOneDeviationDelta, entityResult.playerOneRatingDelta,
            entityResult.playerTwoScore, entityResult.playerTwoDeviationDelta, entityResult.playerTwoRatingDelta
        ) }
        return Game(
            entity.id, entity.leagueId, entity.dateTime,
            entity.playerOneId, entity.playerOneName, entity.playerOneRating, entity.playerOneDeviation,
            entity.playerTwoId, entity.playerTwoName, entity.playerTwoRating, entity.playerTwoDeviation,
            result
        )
    }

    private fun decode(value: String) = String(Base64.getDecoder().decode(value)).toLong()

    private fun encode(timestamp: Long) = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
}