package com.tm.rankme.api

import org.springframework.amqp.core.DirectExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingConfiguration {
    @Bean
    fun exchange(): DirectExchange {
        return DirectExchange("rankme.api")
    }
}