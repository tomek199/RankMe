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
        game.playerOneDeviation = message.firstDeviation
        game.playerOneRating = message.firstRating
        game.playerTwoDeviation = message.secondDeviation
        game.playerTwoRating = message.secondRating
        game.result = result(message)
        repository.store(game)
    }

    private fun create(message: GamePlayedMessage) {
        val firstPlayerName = playerPort.playerName(message.firstId)
        val secondPlayerName = playerPort.playerName(message.secondId)
        val game = Game(message.aggregateId, message.leagueId, LocalDateTime.ofEpochSecond(message.dateTime, 0, ZoneOffset.UTC),
            message.firstId, firstPlayerName, message.firstRating, message.firstDeviation,
            message.secondId, secondPlayerName, message.secondRating, message.secondDeviation,
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
    val firstDeviation: Int,
    val firstDeviationDelta: Int,
    val firstRating: Int,
    val firstRatingDelta: Int,
    val secondId: UUID,
    val secondScore: Int,
    val secondDeviation: Int,
    val secondDeviationDelta: Int,
    val secondRating: Int,
    val secondRatingDelta: Int
)
