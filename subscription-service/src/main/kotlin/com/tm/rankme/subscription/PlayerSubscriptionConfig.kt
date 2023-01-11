package com.tm.rankme.subscription

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.function.Consumer

@Configuration
class PlayerSubscriptionConfig {
    private val log = LoggerFactory.getLogger(PlayerSubscriptionConfig::class.java)

    @Bean
    fun playerCreatedConsumer(playerCreatedSink: Sinks.Many<PlayerCreated>): Consumer<PlayerCreatedMessage> =
        Consumer { inboundMessage: PlayerCreatedMessage ->
            inboundMessage
                .also { log.info("Consumed message $it") }
                .let { playerCreatedSink.tryEmitNext(PlayerCreated(it.id, it.leagueId, it.name, it.deviation, it.rating)) }
        }

    @Bean
    fun playerCreatedSink(): Sinks.Many<PlayerCreated> = Sinks.many().multicast().onBackpressureBuffer()

    @Bean
    fun playerCreatedFlux(playerCreatedSink: Sinks.Many<PlayerCreated>): Flux<PlayerCreated> = playerCreatedSink.asFlux()

    data class PlayerCreatedMessage(
        val id: String,
        val leagueId: String,
        val name: String,
        val deviation: Int,
        val rating: Int
    )
}