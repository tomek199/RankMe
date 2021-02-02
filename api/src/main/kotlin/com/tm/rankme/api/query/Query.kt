package com.tm.rankme.api.query

import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service

@Service
class Query(
    private val buildProperties: BuildProperties
) : GraphQLQueryResolver {

    fun info(): String = "RankMe GraphQL API ${buildProperties.version}"
//    fun getLeague(query: GetLeagueQuery): League? = queryBus.execute(query)
//    fun getPlayer(query: GetPlayerQuery): Player? = queryBus.execute(query)
}