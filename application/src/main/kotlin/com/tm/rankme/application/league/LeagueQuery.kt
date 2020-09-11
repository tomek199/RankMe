package com.tm.rankme.application.league

import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class LeagueQuery(private val leagueService: LeagueService) : GraphQLQueryResolver {
    fun league(id: String): LeagueModel? {
        return leagueService.get(id)
    }
}