package com.tm.rankme.projection

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tm.rankme.model.league.LeagueRepository
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class LeagueRenamedConsumer(
    private val repository: LeagueRepository
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
        val league = repository.byId(message.aggregateId)
        league?.let {
            it.name = message.name
            repository.store(it)
        } ?: log.error("League ${message.aggregateId} cannot be found")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class LeagueRenamedMessage(
    val aggregateId: UUID,
    val name: String
)
