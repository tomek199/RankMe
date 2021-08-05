package com.tm.rankme.command.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.*
import com.tm.rankme.domain.league.League
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class PlayGameHandlerTest {
    private val repository = mockk<GameRepository>()
    private val playerPort = mockk<PlayerPort>()
    private val eventBus = mockk<EventBus>()
    private val handler: Consumer<PlayGameCommand> = PlayGameHandler(repository, playerPort, eventBus)

    @Test
    internal fun `Should create played game`() {
        // given
        val command = PlayGameCommand(randomNanoId(), randomNanoId(), 2, 5)
        val leagueId = randomNanoId()
        val firstResult = Result(command.playerOneScore, 229, -34, 1983, -85)
        val secondResult = Result(command.playerTwoScore, 193, -45, 2314, 79)
        every {
            playerPort.playGame(command.playerOneId, command.playerTwoId, command.playerOneScore, command.playerTwoScore)
        } returns Game.played(leagueId, command.playerOneId, command.playerTwoId, firstResult, secondResult)
        every { repository.store(any()) } just Runs
        every { eventBus.emit(any()) } just Runs
        // when
        handler.accept(command)
        // then
        val gameSlot = slot<Game>()
        verify(exactly = 1) { repository.store(capture(gameSlot)) }
        verify(exactly = 1) {
            playerPort.playGame(command.playerOneId, command.playerTwoId, command.playerOneScore, command.playerTwoScore)
        }
        verify(exactly = 1) { eventBus.emit(ofType<Event<League>>()) }
        gameSlot.captured.let {
            assertEquals(leagueId, it.leagueId)
            assertEquals(Pair(command.playerOneId, command.playerTwoId), it.playerIds)
            assertEquals(Pair(firstResult, secondResult), it.result)
            assertNotNull(it.dateTime)
            assertTrue(it.pendingEvents.last() is GamePlayed)
        }
    }

    @Test
    internal fun `Should throw exception when port throws exception`() {
        // given
        val command = PlayGameCommand(randomNanoId(), randomNanoId(), 3, 2)
        every {
            playerPort.playGame(command.playerOneId, command.playerTwoId, command.playerOneScore, command.playerTwoScore)
        } throws AggregateException("Cannot play game")
        // when
        val exception = assertFailsWith<AggregateException> { handler.accept(command) }
        // then
        assertEquals("Cannot play game", exception.message)
    }
}