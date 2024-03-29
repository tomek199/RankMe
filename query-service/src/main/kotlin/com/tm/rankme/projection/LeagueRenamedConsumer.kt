package com.tm.rankme.projection

import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.league.LeagueRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("leagueRenamedConsumer")
class LeagueRenamedConsumer(
    private val repository: LeagueRepository,
    private val notifier: ModelChangeNotifier
) : Consumer<LeagueRenamedMessage> {

    private val log = LoggerFactory.getLogger(LeagueRenamedConsumer::class.java)

    override fun accept(message: LeagueRenamedMessage) {
        log.info("Consuming message league-renamed for aggregate ${message.aggregateId}")
        val league = repository.byId(message.aggregateId)
        league?.let {
            it.name = message.name
            repository.store(it)
            notifier.notify("league-renamed", it)
        } ?: log.error("League ${message.aggregateId} cannot be found")
    }
}

data class LeagueRenamedMessage(
    val aggregateId: String,
    val name: String
)
