package com.tm.rankme.application.league

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorModel
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.game.GameConnection
import com.tm.rankme.application.game.GameEdge
import com.tm.rankme.application.game.GameModel
import com.tm.rankme.application.game.PageInfo
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class LeagueResolver(
    private val competitorService: CompetitorService,
    private val gameRepository: GameRepository,
    @Qualifier("competitorMapper") private val competitorMapper: Mapper<Competitor, CompetitorModel>,
    @Qualifier("gameMapper") private val gameMapper: Mapper<Game, GameModel>
) : GraphQLResolver<LeagueModel> {

    fun competitors(league: LeagueModel): List<CompetitorModel> {
        return competitorService.getByLeagueId(league.id).map { competitor ->
            competitorMapper.toModel(competitor)
        }
    }

    fun games(league: LeagueModel, last: Int, after: String?): GameConnection {
        val side = gameRepository.findByLeagueId(league.id, last, after)
        val gameEdges = side.content.map { game -> GameEdge(gameMapper.toModel(game), game.id!!) }
        val pageInfo = PageInfo(side.hasPrevious, side.hasNext)
        return GameConnection(side.total, gameEdges, pageInfo)
    }
}
