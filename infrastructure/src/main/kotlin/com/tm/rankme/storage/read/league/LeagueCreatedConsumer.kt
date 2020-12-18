package com.tm.rankme.storage.read.league

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tm.rankme.storage.read.MessageConsumer
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LeagueCreatedConsumer @Autowired constructor(
    private val leagueAccessor: MongoLeagueAccessor
) : MessageConsumer<LeagueCreatedMessage> {

    private val log = LoggerFactory.getLogger(LeagueCreatedConsumer::class.java)

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "league-created-queue"),
            exchange = Exchange(name = "rankme", type = "topic"),
            key = ["league-created"]
        )
    ])
    override fun consume(message: LeagueCreatedMessage) {
        log.info("Consuming message league-created for aggregate ${message.aggregateId}")
        val league = LeagueEntity(message.aggregateId, message.name, message.allowDraws, message.maxScore)
        leagueAccessor.save(league)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class LeagueCreatedMessage(
    val aggregateId: UUID,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int
)