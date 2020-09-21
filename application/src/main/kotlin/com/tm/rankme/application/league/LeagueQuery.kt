package com.tm.rankme.application.league

import com.tm.rankme.application.common.logger
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class LeagueQuery(private val leagueService: LeagueService) : GraphQLQueryResolver {
    private val log = logger<LeagueQuery>()

    fun league(id: String): LeagueModel? {
        log.info("Get league: id=$id")
        return leagueService.get(id)
    }
}