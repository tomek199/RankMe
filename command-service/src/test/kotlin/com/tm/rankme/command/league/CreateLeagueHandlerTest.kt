package com.tm.rankme.command.league

import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class CreateLeagueHandlerTest {
    private val repository = mockk<LeagueRepository>()
    private val eventBus = mockk<EventBus>()
    private val handler: Consumer<CreateLeagueCommand> = CreateLeagueHandler(repository, eventBus)

    @Test
    internal fun `Should create league`() {
        // given
        val command = CreateLeagueCommand("Start Wars")
        every { repository.store(any()) } just Runs
        every { eventBus.emit(any()) } just Runs
        // when
        handler.accept(command)
        // then
        val leagueSlot = slot<League>()
        verify(exactly = 1) { repository.store(capture(leagueSlot)) }
        verify(exactly = 1) { eventBus.emit(ofType<Event<League>>()) }
        assertEquals(command.name, leagueSlot.captured.name)
        assertEquals(0, leagueSlot.captured.version)
        assertTrue(leagueSlot.captured.pendingEvents.last() is LeagueCreated)
    }
}