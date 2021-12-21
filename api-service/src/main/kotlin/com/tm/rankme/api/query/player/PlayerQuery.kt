package com.tm.rankme.api.query.player

import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class PlayerQuery(
    private val queryHandler: PlayerQueryHandler
) : GraphQLQueryResolver {

    fun player(query: GetPlayerQuery): Player? = queryHandler.handle(query)
}