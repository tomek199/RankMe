package com.tm.rankme.application.competitor

import com.tm.rankme.application.league.LeagueService
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class CompetitorMutation(
    private val competitorService: CompetitorService,
    private val leagueService: LeagueService,
) : GraphQLMutationResolver {

    fun addCompetitor(input: AddCompetitorInput): CompetitorModel {
        leagueService.checkIfExist(input.leagueId)
        return competitorService.create(input.leagueId, input.username)
    }
}