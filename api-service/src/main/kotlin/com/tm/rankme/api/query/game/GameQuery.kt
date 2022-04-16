package com.tm.rankme.api.query.game

import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.relay.Connection
import org.springframework.stereotype.Service

@Service
class GameQuery(
    private val queryHandler: GameQueryHandler
) : GraphQLQueryResolver {

    fun games(query: GetGamesForLeagueQuery): Connection<Game> = queryHandler.handle(query)

    fun completedGames(query: GetCompletedGamesForLeagueQuery): Connection<CompletedGame> = queryHandler.handle(query)

    fun scheduledGames(query: GetScheduledGamesForLeagueQuery): Connection<ScheduledGame> = queryHandler.handle(query)

    fun playerGames(query: GetGamesForPlayerQuery): Connection<Game> = queryHandler.handle(query)

    fun playerCompletedGames(query: GetCompletedGamesForPlayerQuery): Connection<CompletedGame> = queryHandler.handle(query)
}