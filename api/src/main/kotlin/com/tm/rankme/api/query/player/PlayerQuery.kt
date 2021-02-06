package com.tm.rankme.api.query.player

import com.tm.rankme.api.query.QueryBus
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Service

@Service
class PlayerQuery(
    private val bus: QueryBus,
    private val mapper: PlayerMapper
) : GraphQLQueryResolver {

    fun getPlayer(query: GetPlayerQuery): Player? = bus.execute(query, mapper)
}