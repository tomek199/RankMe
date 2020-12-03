package com.tm.rankme.storage.write.league

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.storage.write.EventEmitter
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.core.MessagePropertiesBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class LeagueEventEmitter(
    private val template: RabbitTemplate,
    private val exchange: TopicExchange
) : EventEmitter<League> {

    private val objectMapper = jacksonObjectMapper()
    private val messageProperties = MessagePropertiesBuilder.newInstance()
        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
        .build()

    override fun emit(event: Event<League>) {
        val message = Message(objectMapper.writeValueAsBytes(event), messageProperties)
        template.send(exchange.name, event.type, message)
    }
}