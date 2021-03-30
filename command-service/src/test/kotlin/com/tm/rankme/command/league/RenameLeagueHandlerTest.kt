package com.tm.rankme.command.league

import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.*
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class RenameLeagueHandlerTest {
    private val repository = mockk<LeagueRepository>()
    private val eventBus = mockk<EventBus>()
    private val handler: Consumer<RenameLeagueCommand> = RenameLeagueHandler(repository, eventBus)

    @Test
    internal fun `Should change league name`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        val command = RenameLeagueCommand(league.id,"Transformers")
        every { repository.byId(league.id) } returns league
        every { repository.store(any()) } just Runs
        every { eventBus.emit(any()) } just Runs
        // when
        handler.accept(command)
        // then
        val leagueSlot = slot<League>()
        verify(exactly = 1) { repository.byId(league.id) }
        verify(exactly = 1) { repository.store(capture(leagueSlot)) }
        verify(exactly = 1) { eventBus.emit(ofType<Event<League>>()) }
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
        val exception = assertFailsWith<AggregateException> { handler.accept(command) }
        // then
        assertEquals(exceptionMessage, exception.message)
    }
}