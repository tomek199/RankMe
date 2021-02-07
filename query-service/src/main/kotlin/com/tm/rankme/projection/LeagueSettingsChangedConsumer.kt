package com.tm.rankme.projection

import com.tm.rankme.model.league.LeagueRepository
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class LeagueSettingsChangedConsumer(
    private val repository: LeagueRepository
) : MessageConsumer<LeagueSettingsChangedMessage> {

    private val log = LoggerFactory.getLogger(LeagueSettingsChangedConsumer::class.java)

    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(name = "league-settings-changed-queue"),
            exchange = Exchange(name = "rankme", type = "topic"),
            key = ["league-settings-changed"]
        )
    ])
    override fun consume(message: LeagueSettingsChangedMessage) {
        log.info("Consuming message league-settings-changed for aggregate ${message.aggregateId}")
        val league = repository.byId(message.aggregateId)
        league?.let {
            it.allowDraws = message.allowDraws
            it.maxScore = message.maxScore
            repository.store(it)
        } ?: log.error("League ${message.aggregateId} cannot be found")
    }
}

data class LeagueSettingsChangedMessage(
    val aggregateId: UUID,
    val allowDraws: Boolean,
    val maxScore: Int
)
