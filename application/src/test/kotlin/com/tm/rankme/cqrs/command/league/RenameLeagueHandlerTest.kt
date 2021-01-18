package com.tm.rankme.cqrs.command.league

import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class RenameLeagueHandlerTest {
    private val repository = mockk<LeagueRepository>()
    private val handler: CommandHandler<RenameLeagueCommand> = RenameLeagueHandler(repository)

    @Test
    internal fun `Should change league name`() {
        // given
        val name = "Star Wars"
        val league = League.create(name)
        val command = RenameLeagueCommand(league.id,"Transformers")
        every { repository.byId(league.id) } returns league
        every { repository.store(any()) } just Runs
        // when
        handler.dispatch(command)
        // then
        val leagueSlot = slot<League>()
        verify(exactly = 1) { repository.byId(league.id) }
        verify(exactly = 1) { repository.store(capture(leagueSlot)) }
        assertEquals(command.name, leagueSlot.captured.name)
        assertEquals(1, leagueSlot.captured.version)
        assertTrue(leagueSlot.captured.pendingEvents.last() is LeagueRenamed)
    }

    @Test
    internal fun `Should throw exception when league does not exist`() {
        // given
        val id = UUID.randomUUID()
        val exceptionMessage = "League is not found"
        val command = RenameLeagueCommand(id, "Transformers")
        every { repository.byId(id) } throws AggregateException(exceptionMessage)
        // when
        val exception = assertFailsWith<AggregateException> { handler.dispatch(command) }
        // then
        assertEquals(exceptionMessage, exception.message)
    }
}