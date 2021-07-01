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

@Repository
class MongoGameRepository(
    private val accessor: MongoGameAccessor
) : GameRepository {

    override fun byId(id: UUID): Game? = accessor.findByIdOrNull(id.toString())?.let { gameFromEntity(it) }

    override fun store(game: Game) {
        val result = game.result?.let { gameResult ->
            com.tm.rankme.infrastructure.game.Result(
                gameResult.playerOneScore, gameResult.playerOneDeviationDelta, gameResult.playerOneRatingDelta,
                gameResult.playerTwoScore, gameResult.playerTwoDeviationDelta, gameResult.playerTwoRatingDelta
            )
        }
        val entity = GameEntity(
            game.id.toString(), game.leagueId.toString(), game.dateTime,
            game.playerOneId.toString(), game.playerOneName, game.playerOneRating, game.playerOneDeviation,
            game.playerTwoId.toString(), game.playerTwoName, game.playerTwoRating, game.playerTwoDeviation,
            result
        )
        accessor.save(entity)
    }

    override fun byLeagueId(leagueId: UUID, first: Int, after: String?): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page =
            if (after == null) accessor.getByLeagueIdOrderByTimestampDesc(leagueId.toString(), pageable)
            else accessor.getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(leagueId.toString(), decode(after), pageable)
        val games: List<Item<Game>> = page.content.map(this::itemForEntity)
        return Page(games, after != null, page.hasNext())
    }

    override fun byPlayerId(playerId: UUID, first: Int, after: String?): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page =
            if (after == null) accessor.getByPlayerIdOrderByTimestampDesc(playerId.toString(), pageable)
            else accessor.getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(playerId.toString(), decode(after), pageable)
        val games: List<Item<Game>> = page.content.map(this::itemForEntity)
        return Page(games, after != null, page.hasNext())
    }

    private fun itemForEntity(entity: GameEntity) = Item(gameFromEntity(entity), encode(entity.timestamp))

    private fun gameFromEntity(entity: GameEntity): Game {
        val result = entity.result?.let { entityResult -> Result(
            entityResult.playerOneScore, entityResult.playerOneDeviationDelta, entityResult.playerOneRatingDelta,
            entityResult.playerTwoScore, entityResult.playerTwoDeviationDelta, entityResult.playerTwoRatingDelta
        ) }
        return Game(
            UUID.fromString(entity.id), UUID.fromString(entity.leagueId), entity.dateTime,
            UUID.fromString(entity.playerOneId), entity.playerOneName, entity.playerOneRating, entity.playerOneDeviation,
            UUID.fromString(entity.playerTwoId), entity.playerTwoName, entity.playerTwoRating, entity.playerTwoDeviation,
            result
        )
    }

    private fun decode(value: String) = String(Base64.getDecoder().decode(value)).toLong()

    private fun encode(timestamp: Long) = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
}