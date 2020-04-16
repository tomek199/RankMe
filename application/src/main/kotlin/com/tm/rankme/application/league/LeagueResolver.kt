package com.tm.rankme.application.league

import com.coxautodev.graphql.tools.GraphQLResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorModel
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class LeagueResolver(
    private val competitorRepository: CompetitorRepository,
    @Qualifier("competitorMapper") private val competitorMapper: Mapper<Competitor, CompetitorModel>
) : GraphQLResolver<LeagueModel> {

    fun competitors(league: LeagueModel): List<CompetitorModel> {
        return competitorRepository.findByLeagueId(league.id).map { competitor ->
            competitorMapper.toModel(competitor)
        }
    }
}
