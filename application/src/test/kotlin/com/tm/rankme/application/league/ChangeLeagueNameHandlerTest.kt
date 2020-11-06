package com.tm.rankme.application.league

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.tm.rankme.application.cqrs.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueNameChanged
import com.tm.rankme.domain.league.LeagueRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.Mockito.verify

internal class ChangeLeagueNameHandlerTest {
    private val repository: LeagueRepository = mock()
    private val handler: CommandHandler<ChangeLeagueNameCommand> = ChangeLeagueNameHandler(repository)

    @Test
    internal fun `Should change league name`() {
        // given
        val name = "Star Wars"
        val league = League.create(name)
        val command = ChangeLeagueNameCommand(league.id,"Transformers")
        given(repository.byId(league.id)).willReturn(league)
        willDoNothing().given(repository).store(any())
        // when
        handler.dispatch(command)
        // then
        val leagueCaptor = argumentCaptor<League>()
        verify(repository).byId(league.id)
        verify(repository).store(leagueCaptor.capture())
        assertEquals(command.name, leagueCaptor.firstValue.name)
        assertEquals(2, leagueCaptor.firstValue.version)
        assertTrue(leagueCaptor.firstValue.pendingEvents.last() is LeagueNameChanged)
    }

    @Test
    internal fun `Should throw exception when league does not exist`() {
        // given
        val id = UUID.randomUUID()
        val exceptionMessage = "League is not found"
        val command = ChangeLeagueNameCommand(id, "Transformers")
        given(repository.byId(id)).willThrow(AggregateException(exceptionMessage))
        // when
        val exception = assertFailsWith<AggregateException> { handler.dispatch(command) }
        // then
        assertEquals(exceptionMessage, exception.message)
    }
}