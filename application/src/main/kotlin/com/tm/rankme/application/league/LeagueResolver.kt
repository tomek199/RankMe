package com.tm.rankme.application.league

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorModel
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.game.GameModel
import com.tm.rankme.application.game.GameService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game
import graphql.kickstart.tools.GraphQLResolver
import graphql.relay.Connection
import graphql.relay.DefaultConnection
import graphql.relay.DefaultConnectionCursor
import graphql.relay.DefaultEdge
import graphql.relay.DefaultPageInfo
import graphql.relay.SimpleListConnection
import graphql.schema.DataFetchingEnvironment
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.*

@Service
class LeagueResolver(
    private val competitorService: CompetitorService,
    private val gameService: GameService,
    @Qualifier("competitorMapper") private val competitorMapper: Mapper<Competitor, CompetitorModel>,
    @Qualifier("gameMapper") private val gameMapper: Mapper<Game, GameModel>
) : GraphQLResolver<LeagueModel> {

    fun competitors(league: LeagueModel): List<CompetitorModel> {
        return competitorService.getListForLeague(league.id).map { competitor ->
            competitorMapper.toModel(competitor)
        }
    }

    fun games(league: LeagueModel, first: Int, after: String?, env: DataFetchingEnvironment): Connection<GameModel> {
        val decodedAfter = if (after != null) String(Base64.getDecoder().decode(after)) else null
        val side = gameService.getSideForLeague(league.id, first, decodedAfter)
        if (side.content.isEmpty()) return SimpleListConnection<GameModel>(emptyList()).get(env)
        val edges = side.content.map { game ->
            val cursor = DefaultConnectionCursor(encode(game.id))
            DefaultEdge<GameModel>(gameMapper.toModel(game), cursor)
        }
        val startCursor = Base64.getEncoder().encodeToString(side.content.first().id!!.toByteArray())
        val endCursor = Base64.getEncoder().encodeToString(side.content.last().id!!.toByteArray())
        val pageInfo = DefaultPageInfo(
            DefaultConnectionCursor(startCursor), DefaultConnectionCursor(endCursor),
            side.hasPrevious, side.hasNext
        )
        SimpleListConnection<GameModel>(emptyList()).get(env)
        return DefaultConnection<GameModel>(edges, pageInfo)
    }

    private fun encode(value: String?): String {
        value ?: throw IllegalStateException("Value to encode is null")
        return Base64.getEncoder().encodeToString(value.toByteArray())
    }
}
