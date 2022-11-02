package com.tm.rankme.projection

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import com.tm.rankme.model.game.PlayerPort
import io.mockk.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.function.Consumer
import kotlin.test.assertEquals

internal class GamePlayedConsumerTest {
    private val repository: GameRepository = mockk()
    private val notifier: ModelChangeNotifier = mockk()
    private val playerPort: PlayerPort = mockk()
    private val consumer: Consumer<GamePlayedMessage> = GamePlayedConsumer(repository, notifier, playerPort)

    @Test
    internal fun `Should consume 'player-played-game' message for new game`() {
        // given
        val aggregateId = randomNanoId()
        val dateTime = LocalDateTime.now().withNano(0)
        val message = GamePlayedMessage(
            aggregateId, randomNanoId(), dateTime.toEpochSecond(ZoneOffset.UTC),
            randomNanoId(), 1, 264, -45, 1902, -76,
            randomNanoId(), 3, 156, -52, 2351, 83
        )
        val firstPlayerName = "Batman"
        val secondPlayerName = "Superman"
        every { repository.byId(message.aggregateId) } returns null
        every { playerPort.playerName(message.firstId) } returns firstPlayerName
        every { playerPort.playerName(message.secondId) } returns secondPlayerName
        every { repository.store(ofType(Game::class)) } just Runs
        every { notifier.notify("game-played", ofType(Game::class)) } just Runs
        // when
        consumer.accept(message)
        // then
        val createdGameSlot = slot<Game>()
        val notificationGameSlot = slot<Game>()
        verifySequence {
            repository.byId(message.aggregateId)
            playerPort.playerName(message.firstId)
            playerPort.playerName(message.secondId)
            repository.store(capture(createdGameSlot))
            notifier.notify("game-played", capture(notificationGameSlot))
        }
        assertEquals(createdGameSlot.captured, notificationGameSlot.captured)
        createdGameSlot.captured.let {
            assertEquals(message.aggregateId, it.id)
            assertEquals(message.leagueId, it.leagueId)
            assertEquals(dateTime, it.dateTime)
            assertEquals(message.firstId, it.playerOneId)
            assertEquals(firstPlayerName, it.playerOneName)
            assertEquals(message.firstDeviation, it.playerOneDeviation)
            assertEquals(message.firstRating, it.playerOneRating)
            assertEquals(message.firstScore, it.result!!.playerOneScore)
            assertEquals(message.firstDeviationDelta, it.result!!.playerOneDeviationDelta)
            assertEquals(message.firstRatingDelta, it.result!!.playerOneRatingDelta)
            assertEquals(message.secondId, it.playerTwoId)
            assertEquals(secondPlayerName, it.playerTwoName)
            assertEquals(message.secondDeviation, it.playerTwoDeviation)
            assertEquals(message.secondRating, it.playerTwoRating)
            assertEquals(message.secondScore, it.result!!.playerTwoScore)
            assertEquals(message.secondDeviationDelta, it.result!!.playerTwoDeviationDelta)
            assertEquals(message.secondRatingDelta, it.result!!.playerTwoRatingDelta)
        }
    }

    @Test
    internal fun `Should consume 'player-played-game' message for scheduled game`() {
        // given
        val aggregateId = randomNanoId()
        val dateTime = LocalDateTime.now().withNano(0)
        val message = GamePlayedMessage(
            aggregateId, randomNanoId(), dateTime.toEpochSecond(ZoneOffset.UTC),
            randomNanoId(), 1, 264, -45, 1902, -76,
            randomNanoId(), 3, 156, -52, 2351, 83
        )
        val game = Game(
            message.aggregateId, message.leagueId, LocalDateTime.ofInstant(Instant.ofEpochSecond(message.dateTime), ZoneOffset.UTC),
            message.firstId, "Batman", 1978, 309,
            message.secondId, "Superman", 2268, 208
        )
        every { repository.byId(message.aggregateId) } returns game
        every { repository.store(ofType(Game::class)) } just Runs
        every { notifier.notify("game-played", ofType(Game::class)) } just Runs
        // when
        consumer.accept(message)
        // then
        val updatedGameSlot = slot<Game>()
        val notificationGameSlot = slot<Game>()
        verifySequence {
            repository.byId(message.aggregateId)
            repository.store(capture(updatedGameSlot))
            notifier.notify("game-played", capture(notificationGameSlot))
        }
        assertEquals(updatedGameSlot.captured, notificationGameSlot.captured)
        updatedGameSlot.captured.let {
            assertEquals(message.aggregateId, it.id)
            assertEquals(message.leagueId, it.leagueId)
            assertEquals(message.dateTime, it.dateTime.toEpochSecond(ZoneOffset.UTC))
            assertEquals(message.firstId, it.playerOneId)
            assertEquals(game.playerOneName, it.playerOneName)
            assertEquals(264, it.playerOneDeviation)
            assertEquals(1902, it.playerOneRating)
            assertEquals(message.firstScore, it.result!!.playerOneScore)
            assertEquals(message.firstDeviationDelta, it.result!!.playerOneDeviationDelta)
            assertEquals(message.firstRatingDelta, it.result!!.playerOneRatingDelta)
            assertEquals(message.secondId, it.playerTwoId)
            assertEquals(game.playerTwoName, it.playerTwoName)
            assertEquals(156, it.playerTwoDeviation)
            assertEquals(2351, it.playerTwoRating)
            assertEquals(message.secondScore, it.result!!.playerTwoScore)
            assertEquals(message.secondDeviationDelta, it.result!!.playerTwoDeviationDelta)
            assertEquals(message.secondRatingDelta, it.result!!.playerTwoRatingDelta)
        }
    }
}