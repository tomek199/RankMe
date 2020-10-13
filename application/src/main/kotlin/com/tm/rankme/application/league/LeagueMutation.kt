package com.tm.rankme.application.league

import com.tm.rankme.application.common.logger
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class LeagueMutation(private val leagueService: LeagueService) : GraphQLMutationResolver {
    private val log = logger<LeagueMutation>()

    fun addLeague(input: AddLeagueInput): LeagueModel {
        log.info("Add league: $input")
        return leagueService.create(input.name)
    }
}