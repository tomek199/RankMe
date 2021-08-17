package com.tm.rankme.command.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.*
import com.tm.rankme.domain.league.League
import io.mockk.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class CompleteGameHandlerTest {
    private val repository = mockk<GameRepository>()
    private val playerPort = mockk<PlayerPort>()
    private val eventBus = mockk<EventBus>()
    private val handler: Consumer<CompleteGameCommand> = CompleteGameHandler(repository, playerPort, eventBus)

    @Test
    internal fun `Should create played game`() {
        // given
        val command = CompleteGameCommand(randomNanoId(), 3, 2)
        val leagueId = randomNanoId()
        val firstResult = Result(command.playerOneScore, 229, -34, 1983, -85)
        val secondResult = Result(command.playerTwoScore, 193, -45, 2314, 79)
        val game = Game.from(listOf(
            GameScheduled(leagueId, randomNanoId(), randomNanoId(), Instant.now().toEpochMilli(), randomNanoId())
        ))
        every { repository.byId(command.gameId) } returns game
        every { repository.store(any()) } just Runs
        every {
            playerPort.playGame(game.playerIds.first, game.playerIds.second, command.playerOneScore, command.playerTwoScore)
        } returns Game.played(leagueId, game.playerIds.first, game.playerIds.second, firstResult, secondResult)
        every { eventBus.emit(any()) } just Runs
        // when
        handler.accept(command)
        // then
        val gameSlot = slot<Game>()
        verifySequence {
            repository.byId(command.gameId)
            repository.store(capture(gameSlot))
        }
        verify(exactly = 1) {
            playerPort.playGame(game.playerIds.first, game.playerIds.second, command.playerOneScore, command.playerTwoScore)
        }
        verify(exactly = 1) { eventBus.emit(ofType<Event<League>>()) }
        gameSlot.captured.let {
            assertEquals(leagueId, it.leagueId)
            assertEquals(Pair(game.playerIds.first, game.playerIds.second), it.playerIds)
            assertEquals(Pair(firstResult, secondResult), it.result)
            assertNotNull(it.dateTime)
            assertTrue(it.pendingEvents.last() is GamePlayed)
        }
    }

    @Test
    internal fun `Should throw exception when try to complete already played game`() {
        // given
        val command = CompleteGameCommand(randomNanoId(), 3, 4)
        val firstResult = Result(command.playerOneScore, 229, -34, 1983, -85)
        val secondResult = Result(command.playerTwoScore, 193, -45, 2314, 79)
        val game = Game.played(randomNanoId(), randomNanoId(), randomNanoId(), firstResult, secondResult)
        every { repository.byId(command.gameId) } returns game
        // when
        val exception = assertFailsWith<AggregateException> { handler.accept(command) }
        // then
        assertEquals("Game ${game.id} is already played", exception.message)
    }

    @Test
    internal fun `Should throw exception when port throws exception`() {
        // given
        val game = Game.scheduled(randomNanoId(), LocalDateTime.now(), randomNanoId(), randomNanoId())
        val command = CompleteGameCommand(game.id, 3, 2)
        every { repository.byId(command.gameId) } returns game
        every {
            playerPort.playGame(ofType(String::class), ofType(String::class), command.playerOneScore, command.playerTwoScore)
        } throws AggregateException("Cannot complete game")
        // when
        val exception = assertFailsWith<AggregateException> { (handler.accept(command)) }
        // then
        assertEquals("Cannot complete game", exception.message)
    }
}