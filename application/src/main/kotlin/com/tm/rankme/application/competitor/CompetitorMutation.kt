package com.tm.rankme.application.competitor

import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class CompetitorMutation(private val competitorService: CompetitorService) : GraphQLMutationResolver {
    fun addCompetitor(input: AddCompetitorInput): CompetitorModel {
        return competitorService.create(input.leagueId, input.username)
    }
}