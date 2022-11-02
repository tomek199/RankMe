package com.tm.rankme.subscription

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST
import java.util.function.Consumer

@Configuration
class PlayerSubscriptionConfig {
    private val log = LoggerFactory.getLogger(PlayerSubscriptionConfig::class.java)

    @Bean
    fun playerCreatedSink(): Sinks.Many<PlayerCreated> {
        return Sinks.many().multicast().onBackpressureBuffer()
    }

    @Bean
    fun playerCreatedFlux(playerCreatedSink: Sinks.Many<PlayerCreated>): Consumer<Flux<PlayerCreatedMessage>> =
        Consumer { inboundMessage: Flux<PlayerCreatedMessage> ->
            inboundMessage.subscribe {
                log.info("Consumed message $it")
                playerCreatedSink.emitNext(PlayerCreated(it.id, it.name, it.deviation, it.rating), FAIL_FAST)
            }
        }

    data class PlayerCreatedMessage(
        val id: String,
        val name: String,
        val deviation: Int,
        val rating: Int
    )
}