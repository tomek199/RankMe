package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.logger
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class CompetitorQuery(private val competitorService: CompetitorService) : GraphQLQueryResolver {
    private val log = logger<CompetitorQuery>()

    fun competitor(id: String): CompetitorModel? {
        log.info("Get competitor: id=$id")
        return competitorService.get(id)
    }

    fun competitorsByLeagueId(leagueId: String): List<CompetitorModel> {
        log.info("get competitors by league id: leagueId=$leagueId")
        return competitorService.getListForLeague(leagueId)
    }
}
