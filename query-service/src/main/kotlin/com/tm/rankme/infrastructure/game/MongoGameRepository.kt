package com.tm.rankme.infrastructure.game

import com.tm.rankme.model.Item
import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import com.tm.rankme.model.game.Result
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.*
import org.springframework.data.domain.Page as JdbcPage

@Repository
class MongoGameRepository(
    private val accessor: MongoGameAccessor
) : GameRepository {

    override fun byId(id: String): Game? = accessor.findByIdOrNull(id)?.let { gameFromEntity(it) }

    override fun store(game: Game) {
        val result = game.result?.let { gameResult ->
            com.tm.rankme.infrastructure.game.Result(
                gameResult.playerOneScore, gameResult.playerOneDeviationDelta, gameResult.playerOneRatingDelta,
                gameResult.playerTwoScore, gameResult.playerTwoDeviationDelta, gameResult.playerTwoRatingDelta
            )
        }
        val entity = GameEntity(
            game.id, game.leagueId, game.dateTime,
            game.playerOneId, game.playerOneName, game.playerOneRating, game.playerOneDeviation,
            game.playerTwoId, game.playerTwoName, game.playerTwoRating, game.playerTwoDeviation,
            result
        )
        accessor.save(entity)
    }

    override fun byLeagueId(leagueId: String, first: Int, after: String?): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page =
            if (after == null) accessor.getByLeagueIdOrderByTimestampDesc(leagueId, pageable)
            else accessor.getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(leagueId, decode(after), pageable)
        return Page(pageItems(page), after != null, page.hasNext())
    }

    override fun completedByLeagueId(leagueId: String, first: Int, after: String?): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page =
            if (after == null) accessor.getByLeagueIdAndResultNotNullOrderByTimestampDesc(leagueId, pageable)
            else accessor.getByLeagueIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(leagueId, decode(after), pageable)
        return Page(pageItems(page), after != null, page.hasNext())
    }

    override fun scheduledByLeagueId(leagueId: String, first: Int, after: String?): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page =
            if (after == null) accessor.getByLeagueIdAndResultNullOrderByTimestampDesc(leagueId, pageable)
            else accessor.getByLeagueIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(leagueId, decode(after), pageable)
        return Page(pageItems(page), after != null, page.hasNext())
    }

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