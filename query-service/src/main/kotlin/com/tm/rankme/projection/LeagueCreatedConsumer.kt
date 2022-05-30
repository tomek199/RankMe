package com.tm.rankme.projection

import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("leagueCreatedConsumer")
class LeagueCreatedConsumer(
    private val repository: LeagueRepository
) : Consumer<LeagueCreatedMessage> {

    private val log = LoggerFactory.getLogger(LeagueCreatedConsumer::class.java)

    override fun accept(message: LeagueCreatedMessage) {
        log.info("Consuming message league-created for aggregate ${message.aggregateId}")
        val league = League(message.aggregateId, message.name, message.allowDraws, message.maxScore)
        repository.store(league)
    }
}

data class LeagueCreatedMessage(
    val aggregateId: String,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int
)