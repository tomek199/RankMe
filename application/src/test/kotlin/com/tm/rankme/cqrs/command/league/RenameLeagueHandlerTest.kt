package com.tm.rankme.cqrs.command.league

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.tm.rankme.cqrs.command.CommandHandler
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.Mockito.verify

internal class RenameLeagueHandlerTest {
    private val repository: LeagueRepository = mock()
    private val handler: CommandHandler<RenameLeagueCommand> = RenameLeagueHandler(repository)

    @Test
    internal fun `Should change league name`() {
        // given
        val name = "Star Wars"
        val league = League.create(name)
        val command = RenameLeagueCommand(league.id,"Transformers")
        given(repository.byId(league.id)).willReturn(league)
        willDoNothing().given(repository).store(any())
        // when
        handler.dispatch(command)
        // then
        val leagueCaptor = argumentCaptor<League>()
        verify(repository).byId(league.id)
        verify(repository).store(leagueCaptor.capture())
        assertEquals(command.name, leagueCaptor.firstValue.name)
        assertEquals(1, leagueCaptor.firstValue.version)
        assertTrue(leagueCaptor.firstValue.pendingEvents.last() is LeagueRenamed)
    }

    @Test
    internal fun `Should throw exception when league does not exist`() {
        // given
        val id = UUID.randomUUID()
        val exceptionMessage = "League is not found"
        val command = RenameLeagueCommand(id, "Transformers")
        given(repository.byId(id)).willThrow(AggregateException(exceptionMessage))
        // when
        val exception = assertFailsWith<AggregateException> { handler.dispatch(command) }
        // then
        assertEquals(exceptionMessage, exception.message)
    }
}