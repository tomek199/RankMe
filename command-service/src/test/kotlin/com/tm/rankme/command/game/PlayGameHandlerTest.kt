package com.tm.rankme.command.game

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.PlayerPort
import com.tm.rankme.domain.game.Result
import com.tm.rankme.domain.league.League
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class PlayGameHandlerTest {
    private val repository = mockk<GameRepository>()
    private val playerPort = mockk<PlayerPort>()
    private val eventBus = mockk<EventBus>()
    private val handler: com.tm.rankme.command.CommandHandler<PlayGameCommand> = PlayGameHandler(repository, playerPort, eventBus)

    @Test
    internal fun `Should create played game`() {
        // given
        val command = PlayGameCommand(UUID.randomUUID(), UUID.randomUUID(), 2, 5)
        val leagueId = UUID.randomUUID()
        val firstResult = Result(command.playerOneScore, -34, -85)
        val secondResult = Result(command.playerTwoScore, -45, 79)
        every {
            playerPort.playGame(command.playerOneId, command.playerTwoId, command.playerOneScore, command.playerTwoScore)
        } returns Game.played(leagueId, command.playerOneId, command.playerTwoId, firstResult, secondResult)
        every { repository.store(any()) } just Runs
        every { eventBus.emit(any()) } just Runs
        // when
        handler.dispatch(command)
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
        val command = PlayGameCommand(UUID.randomUUID(), UUID.randomUUID(), 3, 2)
        every {
            playerPort.playGame(command.playerOneId, command.playerTwoId, command.playerOneScore, command.playerTwoScore)
        } throws AggregateException("Cannot play game")
        // when
        val exception = assertFailsWith<AggregateException> { (handler.dispatch(command)) }
        // then
        assertEquals("Cannot play game", exception.message)
    }
}