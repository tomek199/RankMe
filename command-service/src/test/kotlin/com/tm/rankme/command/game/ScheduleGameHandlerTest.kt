package com.tm.rankme.command.game

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.domain.game.PlayerPort
import com.tm.rankme.domain.league.League
import io.mockk.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.*

internal class ScheduleGameHandlerTest {
    private val repository = mockk<GameRepository>()
    private val playerPort = mockk<PlayerPort>()
    private val eventBus = mockk<EventBus>()
    private val handler: CommandHandler<ScheduleGameCommand> = ScheduleGameHandler(repository, playerPort, eventBus)

    @Test
    internal fun `Should create played game`() {
        // given
        val command = ScheduleGameCommand(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now())
        val leagueId = UUID.randomUUID()
        every { playerPort.extractLeagueId(command.playerOneId, command.playerTwoId) } returns leagueId
        every { repository.store(any()) } just Runs
        every { eventBus.emit(any()) } just Runs
        // when
        handler.dispatch().accept(command)
        // then
        val gameSlot = slot<Game>()
        verify(exactly = 1) { playerPort.extractLeagueId(command.playerOneId, command.playerTwoId) }
        verify(exactly = 1) { repository.store(capture(gameSlot)) }
        verify(exactly = 1) { eventBus.emit(ofType<Event<League>>()) }
        gameSlot.captured.let {
            assertEquals(leagueId, it.leagueId)
            assertNotNull(it.dateTime)
            assertEquals(Pair(command.playerOneId, command.playerTwoId), it.playerIds)
            assertNull(it.result)
            assertTrue(gameSlot.captured.pendingEvents.last() is GameScheduled)
        }
    }

    @Test
    internal fun `Should throw exception when port throws exception`() {
        // given
        val command = ScheduleGameCommand(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now())
        every {
            playerPort.extractLeagueId(command.playerOneId, command.playerTwoId)
        } throws AggregateException("Cannot extract league id")
        // when
        val exception = assertFailsWith<AggregateException> { handler.dispatch().accept(command) }
        // then
        assertEquals("Cannot extract league id", exception.message)
    }
}