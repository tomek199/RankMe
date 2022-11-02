package com.tm.rankme.projection

import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("playerCreatedConsumer")
class PlayerCreatedConsumer(
    private val repository: PlayerRepository,
    private val notifier: ModelChangeNotifier
) : Consumer<PlayerCreatedMessage> {

    private val log = LoggerFactory.getLogger(PlayerCreatedConsumer::class.java)

    override fun accept(message: PlayerCreatedMessage) {
        log.info("Consuming message player-created for aggregate ${message.aggregateId}")
        Player(message.aggregateId, message.leagueId, message.name, message.deviation, message.rating).let {
            repository.store(it)
            notifier.notify("player-created", it)
        }
    }
}

data class PlayerCreatedMessage(
    val aggregateId: String,
    val leagueId: String,
    val name: String,
    val deviation: Int,
    val rating: Int
)