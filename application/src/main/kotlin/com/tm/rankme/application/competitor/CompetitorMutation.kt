package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.league.LeagueService
import com.tm.rankme.domain.competitor.Competitor
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class CompetitorMutation(
    private val competitorService: CompetitorService,
    private val leagueService: LeagueService,
    private val mapper: Mapper<Competitor, CompetitorModel>
) : GraphQLMutationResolver {

    fun addCompetitor(input: AddCompetitorInput): CompetitorModel {
        leagueService.checkIfExist(input.leagueId)
        val domain = Competitor(input.leagueId, input.username)
        val competitor = competitorService.saveCompetitor(domain)
        return mapper.toModel(competitor)
    }
}