package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.Player
import org.springframework.stereotype.Service

@Service
class GameMapper : Mapper<Game, GameModel> {
    override fun toModel(domain: Game): GameModel {
        val id = domain.id ?: throw IllegalStateException("Game id can't be null!")
        val playerOne = mapPlayer(domain.playerOne)
        val playerTwo = mapPlayer(domain.playerTwo)
        return GameModel(id, playerOne, playerTwo, domain.dateTime)
    }

    private fun mapPlayer(player: Player): PlayerModel {
        return PlayerModel(player.competitorId, player.username, player.rating, player.result.score)
    }
}
