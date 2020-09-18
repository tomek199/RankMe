package com.tm.rankme.application.league

import com.tm.rankme.application.competitor.CompetitorModel
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.game.GameModel
import com.tm.rankme.application.game.GameService
import graphql.kickstart.tools.GraphQLResolver
import graphql.relay.Connection
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Service

@Service
class LeagueResolver(
    private val competitorService: CompetitorService,
    private val gameService: GameService
) : GraphQLResolver<LeagueModel> {

    fun competitors(league: LeagueModel): List<CompetitorModel> {
        return competitorService.getListForLeague(league.id)
    }

    fun games(league: LeagueModel, first: Int, after: String?, env: DataFetchingEnvironment): Connection<GameModel> {
        return gameService.getConnectionForLeague(league.id, first, after, env)
    }
}
