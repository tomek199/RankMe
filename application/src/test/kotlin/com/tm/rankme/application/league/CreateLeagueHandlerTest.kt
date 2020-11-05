package com.tm.rankme.application.league

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.tm.rankme.application.cqrs.CommandHandler
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRepository
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

internal class CreateLeagueHandlerTest {
    private val repository: LeagueRepository = mock(LeagueRepository::class.java)
    private val handler: CommandHandler<CreateLeagueCommand> = CreateLeagueHandler(repository)

    @Test
    internal fun `Should create league`() {
        // given
        val command = CreateLeagueCommand("Start Wars")
        // when
        handler.dispatch(command)
        // then
        val leagueCaptor = argumentCaptor<League>()
        verify(repository).store(leagueCaptor.capture())
        assertEquals(command.name, leagueCaptor.firstValue.name)
        assertTrue(leagueCaptor.firstValue.pendingEvents.last() is LeagueCreated)
    }
}