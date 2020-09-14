package com.tm.rankme.application.game

import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class GameQuery(
    private val gameService: GameService,
) : GraphQLQueryResolver {

    fun game(id: String): GameModel? {
        return gameService.get(id)
    }
}