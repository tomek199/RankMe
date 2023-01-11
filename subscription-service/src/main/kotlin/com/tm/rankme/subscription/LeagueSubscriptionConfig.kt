package com.tm.rankme.subscription

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.function.Consumer

@Configuration
internal class LeagueSubscriptionConfig {
    private val log = LoggerFactory.getLogger(LeagueSubscriptionConfig::class.java)

    @Bean
    fun leagueCreatedConsumer(leagueCreatedSink: Sinks.Many<LeagueCreated>): Consumer<LeagueCreatedMessage> =
        Consumer { inboundMessage: LeagueCreatedMessage ->
            inboundMessage
                .also { log.info("Consumed message $it") }
                .let { leagueCreatedSink.tryEmitNext(LeagueCreated(it.id, it.name, it.allowDraws, it.maxScore)) }
        }

    @Bean
    fun leagueCreatedSink(): Sinks.Many<LeagueCreated> = Sinks.many().multicast().onBackpressureBuffer()

    @Bean
    fun leagueCreatedFlux(leagueCreatedSink: Sinks.Many<LeagueCreated>): Flux<LeagueCreated> = leagueCreatedSink.asFlux()

    data class LeagueCreatedMessage(
        val id: String,
        var name: String,
        var allowDraws: Boolean,
        var maxScore: Int
    )
}