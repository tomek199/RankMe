package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class CompetitorQuery(
    private val competitorService: CompetitorService,
    @Qualifier("competitorMapper") private val mapper: Mapper<Competitor, CompetitorModel>
) : GraphQLQueryResolver {

    fun competitor(id: String): CompetitorModel? {
        val competitor = competitorService.get(id)
        return mapper.toModel(competitor)
    }

    fun competitorsByLeagueId(leagueId: String): List<CompetitorModel> {
        return competitorService.getListForLeague(leagueId).map { competitor -> mapper.toModel(competitor) }
    }
}
