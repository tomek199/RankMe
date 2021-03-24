package com.tm.rankme.projection

import com.tm.rankme.model.league.LeagueRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Consumer

@Service
class LeagueSettingsChangedConsumer(
    private val repository: LeagueRepository
) : MessageConsumer<LeagueSettingsChangedMessage> {

    private val log = LoggerFactory.getLogger(LeagueSettingsChangedConsumer::class.java)

    @Bean("leagueSettingsChangedMessageConsumer")
    override fun consume(): Consumer<LeagueSettingsChangedMessage> = Consumer { message ->
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
