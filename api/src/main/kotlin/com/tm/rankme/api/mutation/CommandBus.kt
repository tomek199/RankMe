package com.tm.rankme.api.mutation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.core.MessagePropertiesBuilder
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class CommandBus(
    private val template: RabbitTemplate,
    private val exchange: DirectExchange
) {
    private val log = LoggerFactory.getLogger(CommandBus::class.java)
    private val objectMapper = jacksonObjectMapper().findAndRegisterModules()
    private val messageProperties = MessagePropertiesBuilder.newInstance()
        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
        .build()

    fun execute(command: Command) {
        log.info("Submit command {}", command)
        val message = Message(objectMapper.writeValueAsBytes(command), messageProperties)
        val routingKey = command::class.simpleName!!
        template.send(exchange.name, routingKey, message)
    }
}