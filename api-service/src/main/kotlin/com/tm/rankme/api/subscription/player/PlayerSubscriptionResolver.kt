package com.tm.rankme.api.subscription.player

import graphql.kickstart.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks

@Service
class PlayerSubscriptionResolver(
    private val playerCreatedSink: Sinks.Many<PlayerCreated>
) : GraphQLSubscriptionResolver {

    fun playerCreated(): Publisher<PlayerCreated> {
        return playerCreatedSink.asFlux()
    }
}