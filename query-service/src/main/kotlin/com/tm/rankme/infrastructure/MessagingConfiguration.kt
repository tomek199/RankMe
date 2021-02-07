package com.tm.rankme.infrastructure

import com.fasterxml.jackson.databind.DeserializationFeature
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
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return Jackson2JsonMessageConverter(objectMapper)
    }

    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange("rankme")
    }
}