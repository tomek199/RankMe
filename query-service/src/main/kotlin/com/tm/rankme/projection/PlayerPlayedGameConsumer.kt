package com.tm.rankme.projection

import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.player.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service("playerPlayedGameConsumer")
class PlayerPlayedGameConsumer(
    private val repository: PlayerRepository,
    private val notifier: ModelChangeNotifier
) : Consumer<PlayerPlayedGameMessage> {

    private val log = LoggerFactory.getLogger(PlayerPlayedGameConsumer::class.java)

    override fun accept(message: PlayerPlayedGameMessage) {
        log.info("Consuming message player-played-game for aggregate ${message.aggregateId}")
        val player = repository.byId(message.aggregateId)
        player?.let {
            it.deviation += message.deviationDelta
            it.rating += message.ratingDelta
            repository.store(it)
            notifier.notify("player-played-game", it)
        } ?: log.error("Player ${message.aggregateId} cannot be found")
    }
}

data class PlayerPlayedGameMessage(
    val aggregateId: String,
    val deviationDelta: Int,
    val ratingDelta: Int
)