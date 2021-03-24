package com.tm.rankme.infrastructure

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class SpringCloudEventBus(private val streamBridge: StreamBridge) : EventBus {
    private val log = LoggerFactory.getLogger(SpringCloudEventBus::class.java)

    override fun emit(event: Event<out AggregateRoot>) {
        log.info("Emitting event ${event.type} for aggregate ${event.aggregateId}")
        val message = MessageBuilder.createMessage(
            event,
            MessageHeaders(mapOf(Pair("type", event::class.simpleName)))
        )
        streamBridge.send("commandQuery-out-0", message)
    }
}