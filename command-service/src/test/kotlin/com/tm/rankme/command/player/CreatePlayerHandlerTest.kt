package com.tm.rankme.command.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.player.LeaguePort
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class CreatePlayerHandlerTest {
    private val repository = mockk<PlayerRepository>()
    private val leaguePort = mockk<LeaguePort>()
    private val eventBus = mockk<EventBus>()
    private val handler: Consumer<CreatePlayerCommand> = CreatePlayerHandler(repository, leaguePort, eventBus)

    @Test
    internal fun `Should create player`() {
        // given
        val command = CreatePlayerCommand(randomNanoId(), "Optimus Prime")
        every { leaguePort.exist(command.leagueId) } returns true
        every { repository.store(any()) } just Runs
        every { eventBus.emit(any()) } just Runs
        // when
        handler.accept(command)
        // then
        val playerSlot = slot<Player>()
        verify(exactly = 1) { repository.store(capture(playerSlot)) }
        verify(exactly = 1) { leaguePort.exist(command.leagueId) }
        verify(exactly = 1) { eventBus.emit(ofType<Event<League>>()) }
        playerSlot.captured.let {
            assertEquals(command.leagueId, it.leagueId)
            assertEquals(command.name, it.name)
            assertEquals(0, it.version)
            assertEquals(350, it.deviation)
            assertEquals(1500, it.rating)
            assertTrue(it.pendingEvents.last() is PlayerCreated)
        }
    }

    @Test
    internal fun `Should throw exception when league does not exist`() {
        // given
        val command = CreatePlayerCommand(randomNanoId(), "Optimus Prime")
        every { leaguePort.exist(command.leagueId) } returns false
        // when
        val exception = assertFailsWith<AggregateException> { handler.accept(command) }
        // then
        assertEquals("Cannot create player. League ${command.leagueId} does not exist", exception.message)
    }
}