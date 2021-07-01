package com.tm.rankme.api.query.player

import com.tm.rankme.api.query.game.Game
import com.tm.rankme.api.query.game.GameQueryHandler
import com.tm.rankme.api.query.game.GetGamesForPlayerQuery
import graphql.kickstart.tools.GraphQLResolver
import graphql.relay.Connection
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Service

@Service
class PlayerResolver(
    private val gameQueryHandler: GameQueryHandler
) : GraphQLResolver<Player> {

    fun games(player: Player, first: Int, after: String?, env: DataFetchingEnvironment): Connection<Game> =
        gameQueryHandler.handle(GetGamesForPlayerQuery(player.id, first, after))
}