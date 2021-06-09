package com.tm.rankme.api.query.game

import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.relay.Connection
import org.springframework.stereotype.Service

@Service
class GameQuery(
    private val queryHandler: GameQueryHandler
) : GraphQLQueryResolver {

    fun getGames(query: GetGamesQuery): Connection<Game> = queryHandler.handle(query)
}