package com.tm.rankme.api.mutation

import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class CommandBus(private val streamBridge: StreamBridge) {
    private val log = LoggerFactory.getLogger(CommandBus::class.java)

    fun execute(command: Command) {
        log.info("Submit command {}", command)
        val message = MessageBuilder.createMessage(
            command,
            MessageHeaders(mapOf(Pair("type", command::class.simpleName)))
        )
        streamBridge.send("apiCommand-out-0", message)
    }
}