package com.tm.rankme.subscription

import graphql.kickstart.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class PlayerSubscriptionResolver(
    private val playerCreatedFlux: Flux<PlayerCreated>
) : GraphQLSubscriptionResolver {

    fun playerCreated(leagueId: String): Publisher<PlayerCreated> {
        return playerCreatedFlux.filter { it.leagueId == leagueId }
    }
}