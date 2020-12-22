package com.tm.rankme.cqrs.command.player

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.player.LeaguePort
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class CreatePlayerHandlerTest {
    private val repository = mockk<PlayerRepository>()
    private val leaguePort = mockk<LeaguePort>()
    private val handler: CommandHandler<CreatePlayerCommand> = CreatePlayerHandler(repository, leaguePort)

    @Test
    internal fun `Should create player`() {
        // given
        val command = CreatePlayerCommand(UUID.randomUUID(), "Optimus Prime")
        every { leaguePort.exist(command.leagueId) } returns true
        every { repository.store(any()) } answers { nothing }
        // when
        handler.dispatch(command)
        // then
        val playerSlot = slot<Player>()
        verify(exactly = 1) { repository.store(capture(playerSlot)) }
        assertEquals(command.leagueId, playerSlot.captured.leagueId)
        assertEquals(command.name, playerSlot.captured.name)
        assertEquals(0, playerSlot.captured.version)
        assertEquals(350, playerSlot.captured.deviation)
        assertEquals(1500, playerSlot.captured.rating)
        assertTrue(playerSlot.captured.pendingEvents.last() is PlayerCreated)
    }

    @Test
    internal fun `Should throw exception when league does not exist`() {
        // given
        val command = CreatePlayerCommand(UUID.randomUUID(), "Optimus Prime")
        every { leaguePort.exist(command.leagueId) } returns false
        // when
        val exception = assertFailsWith<AggregateException> { handler.dispatch(command) }
        // then
        assertEquals("Cannot create player. League ${command.leagueId} does not exist", exception.message)
    }
}