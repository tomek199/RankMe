package com.tm.rankme.api.query.league

import com.tm.rankme.api.query.player.GetPlayersQuery
import com.tm.rankme.api.query.player.Player
import com.tm.rankme.api.query.player.PlayerQueryHandler
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Service

@Service
class LeagueResolver(
    private val playerQueryHandler: PlayerQueryHandler
) : GraphQLResolver<League> {

    fun players(league: League): List<Player> = playerQueryHandler.handle(GetPlayersQuery(league.id))
}