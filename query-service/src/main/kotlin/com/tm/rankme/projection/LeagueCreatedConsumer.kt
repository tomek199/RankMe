package com.tm.rankme.projection

import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("leagueCreatedConsumer")
class LeagueCreatedConsumer(
    private val repository: LeagueRepository,
    private val notifier: ModelChangeNotifier
) : Consumer<LeagueCreatedMessage> {

    private val log = LoggerFactory.getLogger(LeagueCreatedConsumer::class.java)

    override fun accept(message: LeagueCreatedMessage) {
        log.info("Consuming message league-created for aggregate ${message.aggregateId}")
        League(message.aggregateId, message.name, message.allowDraws, message.maxScore).let {
            repository.store(it)
            notifier.notify("league-created", it)
        }
    }
}

data class LeagueCreatedMessage(
    val aggregateId: String,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int
)