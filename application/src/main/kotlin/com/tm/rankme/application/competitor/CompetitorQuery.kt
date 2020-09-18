package com.tm.rankme.application.competitor

import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class CompetitorQuery(private val competitorService: CompetitorService) : GraphQLQueryResolver {
    fun competitor(id: String): CompetitorModel? {
        return competitorService.get(id)
    }

    fun competitorsByLeagueId(leagueId: String): List<CompetitorModel> {
        return competitorService.getListForLeague(leagueId)
    }
}
