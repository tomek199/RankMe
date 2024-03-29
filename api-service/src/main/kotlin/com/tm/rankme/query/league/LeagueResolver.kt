package com.tm.rankme.query.league

import com.tm.rankme.query.game.*
import com.tm.rankme.query.player.GetPlayersQuery
import com.tm.rankme.query.player.Player
import com.tm.rankme.query.player.PlayerQueryHandler
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

    fun games(
        league: League, first: Int, after: String?, before: String?, env: DataFetchingEnvironment
    ): Connection<Game> = gameQueryHandler.handle(GetGamesForLeagueQuery(league.id, first, after, before))

    fun completedGames(
        league: League, first: Int, after: String?, before: String?, env: DataFetchingEnvironment
    ): Connection<CompletedGame> = gameQueryHandler.handle(GetCompletedGamesForLeagueQuery(league.id, first, after, before))

    fun scheduledGames(
        league: League, first: Int, after: String?, before: String?, env: DataFetchingEnvironment
    ): Connection<ScheduledGame> = gameQueryHandler.handle(GetScheduledGamesForLeagueQuery(league.id, first, after, before))
}