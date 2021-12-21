package com.tm.rankme.api.query.league

import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.relay.Connection
import org.springframework.stereotype.Service

@Service
class LeagueQuery(
    private val queryHandler: LeagueQueryHandler
) : GraphQLQueryResolver {

    fun league(query: GetLeagueQuery): League? = queryHandler.handle(query)

    fun leagues(query: GetLeaguesQuery): Connection<League> = queryHandler.handle(query)
}