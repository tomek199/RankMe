package com.tm.rankme.storage.write

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventEmitter
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.core.MessagePropertiesBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class RabbitMqEventEmitter(
    private val template: RabbitTemplate,
    private val exchange: TopicExchange
) : EventEmitter {

    private val log = LoggerFactory.getLogger(RabbitMqEventEmitter::class.java)
    private val objectMapper = jacksonObjectMapper()
    private val messageProperties = MessagePropertiesBuilder.newInstance()
        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
        .build()

    override fun emit(event: Event<out AggregateRoot>) {
        log.info("Emitting event ${event.type} for aggregate ${event.aggregateId}")
        val message = Message(objectMapper.writeValueAsBytes(event), messageProperties)
        template.send(exchange.name, event.type, message)
    }
}