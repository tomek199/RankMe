package com.tm.rankme.projection

import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import com.tm.rankme.model.game.PlayerPort
import com.tm.rankme.model.game.Result
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.function.Consumer

@Service("gamePlayedMessageConsumer")
class GamePlayedConsumer(
    private val repository: GameRepository,
    private val playerPort: PlayerPort
) : Consumer<GamePlayedMessage> {

    private val log = LoggerFactory.getLogger(GamePlayedConsumer::class.java)

    override fun accept(message: GamePlayedMessage) {
        log.info("Consuming message game-played for aggregate ${message.aggregateId}")
        repository.byId(message.aggregateId)?.let {
            complete(it, message)
        } ?: create(message)
    }

    private fun complete(game: Game, message: GamePlayedMessage) {
        game.playerOneDeviation += message.firstDeviationDelta
        game.playerOneRating += message.firstRatingDelta
        game.playerTwoDeviation += message.secondDeviationDelta
        game.playerTwoRating += message.secondRatingDelta
        game.result = result(message)
        repository.store(game)
    }

    private fun create(message: GamePlayedMessage) {
        val firstPlayerInfo = playerPort.playerInfo(message.firstId)
        val secondPlayerInfo = playerPort.playerInfo(message.secondId)
        val game = Game(message.aggregateId, message.leagueId, LocalDateTime.ofEpochSecond(message.dateTime, 0, ZoneOffset.UTC),
            message.firstId, firstPlayerInfo.name, firstPlayerInfo.rating, firstPlayerInfo.deviation,
            message.secondId, secondPlayerInfo.name, secondPlayerInfo.rating, secondPlayerInfo.deviation,
            result(message)
        )
        repository.store(game)
    }

    private fun result(message: GamePlayedMessage) = Result(
        message.firstScore, message.firstDeviationDelta, message.firstRatingDelta,
        message.secondScore, message.secondDeviationDelta, message.secondRatingDelta
    )
}

data class GamePlayedMessage(
    val aggregateId: UUID,
    val leagueId: UUID,
    val dateTime: Long,
    val firstId: UUID,
    val firstScore: Int,
    val firstDeviationDelta: Int,
    val firstRatingDelta: Int,
    val secondId: UUID,
    val secondScore: Int,
    val secondDeviationDelta: Int,
    val secondRatingDelta: Int
)
