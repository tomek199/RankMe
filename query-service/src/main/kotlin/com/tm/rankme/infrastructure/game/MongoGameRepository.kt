package com.tm.rankme.infrastructure.game

import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import com.tm.rankme.model.game.Result
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class MongoGameRepository(
    private val accessor: MongoGameAccessor
) : GameRepository {

    override fun byId(id: UUID): Game? = accessor.findByIdOrNull(id)?.let {
        val result = it.result?.let { entityResult -> Result(
            entityResult.playerOneScore, entityResult.playerOneDeviationDelta, entityResult.playerOneRatingDelta,
            entityResult.playerTwoScore, entityResult.playerTwoDeviationDelta, entityResult.playerTwoRatingDelta
        ) }
        Game(
            it.id, it.leagueId, it.dateTime,
            it.playerOneId, it.playerOneName, it.playerOneRating, it.playerOneDeviation,
            it.playerTwoId, it.playerTwoName, it.playerTwoRating, it.playerTwoDeviation,
            result
        )
    }

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
}