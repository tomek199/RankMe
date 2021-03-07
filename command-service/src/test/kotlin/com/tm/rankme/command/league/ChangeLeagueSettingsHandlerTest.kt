package com.tm.rankme.command.league

import com.tm.rankme.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.domain.league.LeagueSettingsChanged
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

internal class ChangeLeagueSettingsHandlerTest {
    private val repository = mockk<LeagueRepository>()
    private val eventBus = mockk<EventBus>()
    private val handler: com.tm.rankme.command.CommandHandler<ChangeLeagueSettingsCommand> = ChangeLeagueSettingsHandler(repository, eventBus)

    @Test
    internal fun `Should change league name`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        val command = ChangeLeagueSettingsCommand(league.id, true, 5)
        every { repository.byId(league.id) } returns league
        every { repository.store(any()) } just Runs
        every { eventBus.emit(any()) } just Runs
        // when
        handler.dispatch(command)
        // then
        val leagueSlot = slot<League>()
        verify(exactly = 1) { repository.byId(league.id) }
        verify(exactly = 1) { repository.store(capture(leagueSlot)) }
        verify(exactly = 1) { eventBus.emit(ofType<Event<League>>()) }
        assertEquals(command.allowDraws, leagueSlot.captured.settings.allowDraws)
        assertEquals(command.maxScore, leagueSlot.captured.settings.maxScore)
        assertEquals(1, leagueSlot.captured.version)
        assertTrue(leagueSlot.captured.pendingEvents.last() is LeagueSettingsChanged)
    }

    @Test
    internal fun `Should throw exception when league does not exist`() {
        // given
        val id = UUID.randomUUID()
        val exceptionMessage = "League is not found"
        val command = ChangeLeagueSettingsCommand(id, true, 5)
        every { repository.byId(id) } throws AggregateException(exceptionMessage)
        // when
        val exception = assertFailsWith<AggregateException> { handler.dispatch(command) }
        // then
        assertEquals(exceptionMessage, exception.message)
    }
}