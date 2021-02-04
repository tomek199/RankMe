package com.tm.rankme.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingConfiguration {
    @Bean
    fun jsonMessageConverter(): MessageConverter {
        val objectMapper = jacksonObjectMapper().findAndRegisterModules()
        return Jackson2JsonMessageConverter(objectMapper)
    }

    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange("rankme")
    }
}