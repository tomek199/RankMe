package com.tm.rankme.storage.write

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MessagingConfiguration {
    @Bean
    open fun jsonMessageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter(jacksonObjectMapper())
    }

    @Bean
    open fun exchange(): TopicExchange {
        return TopicExchange("rankme")
    }
}