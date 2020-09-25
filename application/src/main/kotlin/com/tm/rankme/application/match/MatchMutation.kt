package com.tm.rankme.application.match

import com.tm.rankme.application.common.logger
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class MatchMutation(private val matchService: MatchService) : GraphQLMutationResolver {
    private val log = logger<MatchMutation>()

    fun addMatch(input: AddMatchInput): MatchModel {
        log.info("Add match: $input")
        return matchService.create(input.leagueId, input.memberOneId, input.memberTwoId, input.dateTime)
    }
}
