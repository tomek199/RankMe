package com.tm.rankme.api.query.player

import com.tm.rankme.api.query.game.*
import graphql.kickstart.tools.GraphQLResolver
import graphql.relay.Connection
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Service

@Service
class PlayerResolver(
    private val gameQueryHandler: GameQueryHandler
) : GraphQLResolver<Player> {

    fun games(
        player: Player, first: Int, after: String?, before: String?, env: DataFetchingEnvironment
    ): Connection<Game> = gameQueryHandler.handle(GetGamesForPlayerQuery(player.id, first, after, before))

    fun completedGames(
        player: Player, first: Int, after: String?, before: String?, env: DataFetchingEnvironment
    ): Connection<CompletedGame> = gameQueryHandler.handle(GetCompletedGamesForPlayerQuery(player.id, first, after, before))

    fun scheduledGames(
        player: Player, first: Int, after: String?, before: String?, env: DataFetchingEnvironment
    ): Connection<ScheduledGame> = gameQueryHandler.handle(GetScheduledGamesForPlayerQuery(player.id, first, after, before))
}