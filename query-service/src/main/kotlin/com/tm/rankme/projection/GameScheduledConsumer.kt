package com.tm.rankme.projection

import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import com.tm.rankme.model.game.PlayerPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.function.Consumer

@Service("gameScheduledMessageConsumer")
class GameScheduledConsumer(
    private val repository: GameRepository,
    private val playerPort: PlayerPort
) : Consumer<GameScheduledMessage> {

    private val log = LoggerFactory.getLogger(GameScheduledConsumer::class.java)

    override fun accept(message: GameScheduledMessage) {
        log.info("Consuming message game-scheduled for aggregate ${message.aggregateId}")
        val firstPlayerInfo = playerPort.playerInfo(message.firstId)
        val secondPlayerInfo = playerPort.playerInfo(message.secondId)
        val game = Game(message.aggregateId, message.leagueId, LocalDateTime.ofEpochSecond(message.dateTime, 0, ZoneOffset.UTC),
            message.firstId, firstPlayerInfo.name, firstPlayerInfo.rating, firstPlayerInfo.deviation,
            message.secondId, secondPlayerInfo.name, secondPlayerInfo.rating, secondPlayerInfo.deviation
        )
        repository.store(game)
    }
}

data class GameScheduledMessage(
    val aggregateId: UUID,
    val leagueId: UUID,
    val dateTime: Long,
    val firstId: UUID,
    val secondId: UUID
)