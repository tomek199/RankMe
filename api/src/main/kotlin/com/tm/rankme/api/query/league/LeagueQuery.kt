package com.tm.rankme.api.query.league

import com.tm.rankme.api.query.QueryBus
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class LeagueQuery(
    private val bus: QueryBus,
    private val mapper: LeagueMapper
) : GraphQLQueryResolver {

    fun getLeague(query: GetLeagueQuery): League? = bus.execute(query, mapper)
}