package com.tm.rankme.projection

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class LeagueCreatedConsumer(
    private val repository: LeagueRepository
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
        val league = League(message.aggregateId, message.name, message.allowDraws, message.maxScore)
        repository.store(league)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class LeagueCreatedMessage(
    val aggregateId: UUID,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int
)