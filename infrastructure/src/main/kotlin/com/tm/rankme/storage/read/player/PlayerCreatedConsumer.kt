package com.tm.rankme.storage.read.player

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tm.rankme.storage.read.MessageConsumer
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class PlayerCreatedConsumer(
    private val playerAccessor: MongoPlayerAccessor
) : MessageConsumer<PlayerCreatedMessage> {

    private val log = LoggerFactory.getLogger(PlayerCreatedConsumer::class.java)

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "player-created-queue"),
            exchange = Exchange(name = "rankme", type = "topic"),
            key = ["player-created"]
        )
    ])
    override fun consume(message: PlayerCreatedMessage) {
        log.info("Consuming message player-created for aggregate ${message.aggregateId}")
        val player = PlayerEntity(message.aggregateId, message.leagueId, message.name, message.deviation, message.rating)
        playerAccessor.save(player)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlayerCreatedMessage(
    val aggregateId: UUID,
    val leagueId: UUID,
    val name: String,
    val deviation: Int,
    val rating: Int
)