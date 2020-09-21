package com.tm.rankme.application.game

import com.tm.rankme.application.common.logger
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class GameQuery(private val gameService: GameService) : GraphQLQueryResolver {
    private val log = logger<GameQuery>()

    fun game(id: String): GameModel? {
        log.info("Get game: id=$id")
        return gameService.get(id)
    }
}