package com.tm.rankme.projection

import com.tm.rankme.model.player.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Consumer

@Service("playerPlayedGameMessageConsumer")
class PlayerPlayedGameConsumer(
    private val repository: PlayerRepository
) : Consumer<PlayerPlayedGameMessage> {

    private val log = LoggerFactory.getLogger(PlayerPlayedGameConsumer::class.java)

    override fun accept(message: PlayerPlayedGameMessage) {
        log.info("Consuming message player-played-game for aggregate ${message.aggregateId}")
        val player = repository.byId(message.aggregateId)
        player?.let {
            it.deviation += message.deviationDelta
            it.rating += message.ratingDelta
            repository.store(it)
        } ?: log.error("Player ${message.aggregateId} cannot be found")
    }
}

data class PlayerPlayedGameMessage(
    val aggregateId: UUID,
    val deviationDelta: Int,
    val ratingDelta: Int
)