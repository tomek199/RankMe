package com.tm.rankme.subscription

import graphql.kickstart.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class LeagueSubscriptionResolver(
    private val leagueCreatedFlux: Flux<LeagueCreated>
) : GraphQLSubscriptionResolver {

    fun leagueCreated(name: String): Publisher<LeagueCreated> {
        return leagueCreatedFlux.filter { it.name == name }
    }
}