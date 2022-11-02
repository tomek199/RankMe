package com.tm.rankme.subscription

import graphql.kickstart.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks

@Service
class PlayerSubscriptionResolver(
    private val playerCreatedSink: Sinks.Many<PlayerCreated>
) : GraphQLSubscriptionResolver {

    fun playerCreated(leagueId: String): Publisher<PlayerCreated> {
        return playerCreatedSink.asFlux().filter { it.leagueId == leagueId }
    }
}