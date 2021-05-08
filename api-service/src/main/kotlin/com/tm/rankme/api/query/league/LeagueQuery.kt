package com.tm.rankme.api.query.league

import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class LeagueQuery(
    private val queryHandler: LeagueQueryHandler
) : GraphQLQueryResolver {

    fun getLeague(query: GetLeagueQuery): League? = queryHandler.handle(query)
}