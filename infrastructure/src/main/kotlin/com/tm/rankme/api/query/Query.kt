package com.tm.rankme.api.query

import com.tm.rankme.cqrs.query.QueryBus
import com.tm.rankme.cqrs.query.league.GetLeagueQuery
import com.tm.rankme.model.league.League
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service

@Service
class Query @Autowired constructor(
    private val buildProperties: BuildProperties,
    private val queryBus: QueryBus
) : GraphQLQueryResolver {

    fun info(): String = "RankMe GraphQL API ${buildProperties.version}"

    fun getLeague(query: GetLeagueQuery): League? = queryBus.execute(query)
}