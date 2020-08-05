package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.game.Game
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class GameQuery(
    private val gameService: GameService,
    @Qualifier("gameMapper") private val mapper: Mapper<Game, GameModel>
) : GraphQLQueryResolver {

    fun game(id: String): GameModel? {
        val game = gameService.get(id)
        return mapper.toModel(game)
    }
}