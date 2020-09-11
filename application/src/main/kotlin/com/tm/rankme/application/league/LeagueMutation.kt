package com.tm.rankme.application.league

import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class LeagueMutation(private val leagueService: LeagueService) : GraphQLMutationResolver {
    fun addLeague(input: AddLeagueInput): LeagueModel {
        return leagueService.create(input.name)
    }
}