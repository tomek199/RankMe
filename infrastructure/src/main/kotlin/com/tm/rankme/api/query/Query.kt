package com.tm.rankme.api.query

import com.tm.rankme.cqrs.query.QueryBus
import com.tm.rankme.cqrs.query.league.GetLeagueQuery
import com.tm.rankme.cqrs.query.player.GetPlayerQuery
import com.tm.rankme.model.league.League
import com.tm.rankme.model.player.Player
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service

@Service
class Query(
    private val buildProperties: BuildProperties,
    private val queryBus: QueryBus
) : GraphQLQueryResolver {

    fun info(): String = "RankMe GraphQL API ${buildProperties.version}"

    fun getLeague(query: GetLeagueQuery): League? = queryBus.execute(query)

    fun getPlayer(query: GetPlayerQuery): Player? = queryBus.execute(query)
}