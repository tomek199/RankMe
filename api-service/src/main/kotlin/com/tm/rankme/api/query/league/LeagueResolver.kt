package com.tm.rankme.api.query.league

import com.tm.rankme.api.query.game.Game
import com.tm.rankme.api.query.game.GameQueryHandler
import com.tm.rankme.api.query.game.GetGamesForLeagueQuery
import com.tm.rankme.api.query.player.GetPlayersQuery
import com.tm.rankme.api.query.player.Player
import com.tm.rankme.api.query.player.PlayerQueryHandler
import graphql.kickstart.tools.GraphQLResolver
import graphql.relay.Connection
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Service

@Service
class LeagueResolver(
    private val playerQueryHandler: PlayerQueryHandler,
    private val gameQueryHandler: GameQueryHandler
) : GraphQLResolver<League> {

    fun players(league: League): List<Player> = playerQueryHandler.handle(GetPlayersQuery(league.id))

    fun games(league: League, first: Int, after: String?, env: DataFetchingEnvironment): Connection<Game> =
        gameQueryHandler.handle(GetGamesForLeagueQuery(league.id, first, after))
}