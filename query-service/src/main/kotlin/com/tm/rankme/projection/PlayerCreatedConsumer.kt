package com.tm.rankme.projection

import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class PlayerCreatedConsumer(
    private val repository: PlayerRepository
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
        val player = Player(message.aggregateId, message.leagueId, message.name, message.deviation, message.rating)
        repository.store(player)
    }
}

data class PlayerCreatedMessage(
    val aggregateId: UUID,
    val leagueId: UUID,
    val name: String,
    val deviation: Int,
    val rating: Int
)