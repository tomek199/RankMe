package com.tm.rankme.projection

import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import com.tm.rankme.model.game.PlayerInfo
import com.tm.rankme.model.game.PlayerPort
import io.mockk.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class GamePlayedConsumerTest {
    private val repository: GameRepository = mockk()
    private val playerPort: PlayerPort = mockk()
    private val consumer: Consumer<GamePlayedMessage> = GamePlayedConsumer(repository, playerPort)

    @Test
    internal fun `Should consume 'player-played-game' message for new game`() {
        // given
        val aggregateId = UUID.randomUUID()
        val message = GamePlayedMessage(
            aggregateId, UUID.randomUUID(), Instant.now().toEpochMilli(),
            UUID.randomUUID(), 1, -45, -76,
            UUID.randomUUID(), 3, -52, 83
        )
        val firstPlayerInfo = PlayerInfo("Batman", 248, 1783)
        val secondPlayerInfo = PlayerInfo("Superman", 153, 2349)
        every { repository.byId(message.aggregateId) } returns null
        every { playerPort.playerInfo(message.firstId) } returns firstPlayerInfo
        every { playerPort.playerInfo(message.secondId) } returns secondPlayerInfo
        every { repository.store(ofType(Game::class)) } just Runs
        // when
        consumer.accept(message)
        // then
        val gameSlot = slot<Game>()
        verifySequence {
            repository.byId(message.aggregateId)
            playerPort.playerInfo(message.firstId)
            playerPort.playerInfo(message.secondId)
            repository.store(capture(gameSlot))
        }
        gameSlot.captured.let {
            assertEquals(message.aggregateId, it.id)
            assertEquals(message.leagueId, it.leagueId)
            assertEquals(message.dateTime, it.dateTime.toEpochSecond(ZoneOffset.UTC))
            assertEquals(message.firstId, it.playerOneId)
            assertEquals(firstPlayerInfo.name, it.playerOneName)
            assertEquals(firstPlayerInfo.deviation, it.playerOneDeviation)
            assertEquals(firstPlayerInfo.rating, it.playerOneRating)
            assertEquals(message.firstScore, it.result!!.playerOneScore)
            assertEquals(message.firstDeviationDelta, it.result!!.playerOneDeviationDelta)
            assertEquals(message.firstRatingDelta, it.result!!.playerOneRatingDelta)
            assertEquals(message.secondId, it.playerTwoId)
            assertEquals(secondPlayerInfo.name, it.playerTwoName)
            assertEquals(secondPlayerInfo.deviation, it.playerTwoDeviation)
            assertEquals(secondPlayerInfo.rating, it.playerTwoRating)
            assertEquals(message.secondScore, it.result!!.playerTwoScore)
            assertEquals(message.secondDeviationDelta, it.result!!.playerTwoDeviationDelta)
            assertEquals(message.secondRatingDelta, it.result!!.playerTwoRatingDelta)
        }
    }

    @Test
    internal fun `Should consume 'player-played-game' message for scheduled game`() {
        // given
        val aggregateId = UUID.randomUUID()
        val message = GamePlayedMessage(
            aggregateId, UUID.randomUUID(), Instant.now().toEpochMilli(),
            UUID.randomUUID(), 1, -45, -76,
            UUID.randomUUID(), 3, -52, 83
        )
        val game = Game(
            message.aggregateId, message.leagueId, LocalDateTime.ofEpochSecond(message.dateTime, 0, ZoneOffset.UTC),
            message.firstId, "Batman", 1783, 248,
            message.secondId, "Superman", 2349, 153
        )
        every { repository.byId(message.aggregateId) } returns game
        every { repository.store(ofType(Game::class)) } just Runs
        // when
        consumer.accept(message)
        // then
        val gameSlot = slot<Game>()
        verifySequence {
            repository.byId(message.aggregateId)
            repository.store(capture(gameSlot))
        }
        gameSlot.captured.let {
            assertEquals(message.aggregateId, it.id)
            assertEquals(message.leagueId, it.leagueId)
            assertEquals(message.dateTime, it.dateTime.toEpochSecond(ZoneOffset.UTC))
            assertEquals(message.firstId, it.playerOneId)
            assertEquals(game.playerOneName, it.playerOneName)
            assertEquals(203, it.playerOneDeviation)
            assertEquals(1707, it.playerOneRating)
            assertEquals(message.firstScore, it.result!!.playerOneScore)
            assertEquals(message.firstDeviationDelta, it.result!!.playerOneDeviationDelta)
            assertEquals(message.firstRatingDelta, it.result!!.playerOneRatingDelta)
            assertEquals(message.secondId, it.playerTwoId)
            assertEquals(game.playerTwoName, it.playerTwoName)
            assertEquals(101, it.playerTwoDeviation)
            assertEquals(2432, it.playerTwoRating)
            assertEquals(message.secondScore, it.result!!.playerTwoScore)
            assertEquals(message.secondDeviationDelta, it.result!!.playerTwoDeviationDelta)
            assertEquals(message.secondRatingDelta, it.result!!.playerTwoRatingDelta)
        }
    }
}