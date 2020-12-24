package com.tm.rankme.storage.read.league

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tm.rankme.storage.read.MessageConsumer
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LeagueRenamedConsumer(
    private val leagueAccessor: MongoLeagueAccessor
) : MessageConsumer<LeagueRenamedMessage> {

    private val log = LoggerFactory.getLogger(LeagueRenamedConsumer::class.java)

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "league-renamed-queue"),
            exchange = Exchange(name = "rankme", type = "topic"),
            key = ["league-renamed"]
        )
    ])
    override fun consume(message: LeagueRenamedMessage) {
        log.info("Consuming message league-renamed for aggregate ${message.aggregateId}")
        val entity = leagueAccessor.findByIdOrNull(message.aggregateId)
        entity?.let {
            it.name = message.name
            leagueAccessor.save(it)
        } ?: log.error("League ${message.aggregateId} cannot be found")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class LeagueRenamedMessage(
    val aggregateId: UUID,
    val name: String
)
