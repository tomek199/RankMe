package com.tm.rankme.projection

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.ModelChangeNotifier
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import com.tm.rankme.model.game.PlayerInfo
import com.tm.rankme.model.game.PlayerPort
import io.mockk.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GameScheduledConsumerTest {
    private val repository: GameRepository = mockk()
    private val notifier: ModelChangeNotifier = mockk()
    private val playerPort: PlayerPort = mockk()
    private val consumer: Consumer<GameScheduledMessage> = GameScheduledConsumer(repository, notifier, playerPort)

    @Test
    internal fun `Should consume 'game-scheduled' message`() {
        // given
        val aggregateId = randomNanoId()
        val dateTime = LocalDateTime.now().withNano(0)
        val message = GameScheduledMessage(
            aggregateId, randomNanoId(), dateTime.toEpochSecond(ZoneOffset.UTC),
            randomNanoId(), randomNanoId()
        )
        val firstPlayerInfo = PlayerInfo("Batman", 275, 1836)
        val secondPlayerInfo = PlayerInfo("Superman", 192, 2173)
        every { playerPort.playerInfo(message.firstId) } returns firstPlayerInfo
        every { playerPort.playerInfo(message.secondId) } returns secondPlayerInfo
        every { repository.store(ofType(Game::class)) } just Runs
        every { notifier.notify("game-scheduled", ofType(Game::class)) } just Runs
        // when
        consumer.accept(message)
        // then
        val createdGameSlot = slot<Game>()
        val notificationGameSlot = slot<Game>()
        verifySequence {
            playerPort.playerInfo(message.firstId)
            playerPort.playerInfo(message.secondId)
            repository.store(capture(createdGameSlot))
            notifier.notify("game-scheduled", capture(notificationGameSlot))
        }
        assertEquals(createdGameSlot.captured, notificationGameSlot.captured)
        createdGameSlot.captured.let {
            assertEquals(message.aggregateId, it.id)
            assertEquals(message.leagueId, it.leagueId)
            assertEquals(dateTime, it.dateTime)
            assertEquals(message.firstId, it.playerOneId)
            assertEquals(firstPlayerInfo.name, it.playerOneName)
            assertEquals(firstPlayerInfo.deviation, it.playerOneDeviation)
            assertEquals(firstPlayerInfo.rating, it.playerOneRating)
            assertEquals(message.secondId, it.playerTwoId)
            assertEquals(secondPlayerInfo.name, it.playerTwoName)
            assertEquals(secondPlayerInfo.deviation, it.playerTwoDeviation)
            assertEquals(secondPlayerInfo.rating, it.playerTwoRating)
            assertNull(it.result)
        }
    }
}