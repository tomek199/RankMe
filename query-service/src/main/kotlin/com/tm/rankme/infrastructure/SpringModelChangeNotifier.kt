package com.tm.rankme.infrastructure

import com.tm.rankme.model.ModelChangeNotifier
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class SpringModelChangeNotifier(private val streamBridge: StreamBridge) : ModelChangeNotifier {
    private val log = LoggerFactory.getLogger(SpringModelChangeNotifier::class.java)

    override fun notify(type: String, model: Any) {
        log.info("Notify subscribers $type: $model")
        val message = MessageBuilder.createMessage(
            model,
            MessageHeaders(mapOf(Pair("type", "query-$type")))
        )
        streamBridge.send("notifications-out-0", message)
    }
}
