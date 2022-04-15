package com.tm.rankme.infrastructure.game

import com.tm.rankme.infrastructure.encode
import com.tm.rankme.model.Item
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.Result

abstract class MongoGamesPage {
    protected fun itemForEntity(entity: GameEntity) = Item(gameFromEntity(entity), encode(entity.timestamp))

    protected fun gameFromEntity(entity: GameEntity): Game {
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
}