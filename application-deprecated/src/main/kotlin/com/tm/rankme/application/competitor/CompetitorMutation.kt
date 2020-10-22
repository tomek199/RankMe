package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.logger
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class CompetitorMutation(private val competitorService: CompetitorService) : GraphQLMutationResolver {
    private val log = logger<CompetitorMutation>()

    fun addCompetitor(input: AddCompetitorInput): CompetitorModel {
        log.info("Add competitor: $input")
        return competitorService.create(input.leagueId, input.username)
    }
}