package com.tm.rankme.api.query

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.core.MessagePropertiesBuilder
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class QueryBus(
    private val template: RabbitTemplate,
    private val exchange: DirectExchange,
) {
    private val log = LoggerFactory.getLogger(QueryBus::class.java)
    private val objectMapper = jacksonObjectMapper().findAndRegisterModules()
    private val messageProperties = MessagePropertiesBuilder.newInstance()
        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
        .build()

    fun <R> execute(query: Query, mapper: Mapper<R>): R? {
        log.info("Submit query {}", query)
        val message = Message(objectMapper.writeValueAsBytes(query), messageProperties)
        val routingKey = query::class.simpleName!!
        val response = template.sendAndReceive(exchange.name, routingKey, message)
        return response?.let {
            log.info("Get query response {}", it.body)
            mapper.deserialize(it.body)
        }
    }
}