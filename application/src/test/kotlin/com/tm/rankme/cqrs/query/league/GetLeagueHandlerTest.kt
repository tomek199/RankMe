package com.tm.rankme.cqrs.query.league

import com.tm.rankme.cqrs.query.QueryHandler
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class GetLeagueHandlerTest {
    private val repository = mockk<LeagueRepository>()
    private val handler: QueryHandler<GetLeagueQuery, League?> = GetLeagueHandler(repository)

    @Test
    internal fun `Should return league`() {
        // given
        val query = GetLeagueQuery(UUID.randomUUID())
        val expectedLeague = League(query.id, "Star Wars", false, 4)
        every { repository.byId(query.id) } returns expectedLeague
        // when
        val league = handler.handle(query)
        // then
        verify(exactly = 1) { repository.byId(query.id) }
        assertNotNull(league)
        assertEquals(query.id, league.id)
        assertEquals(expectedLeague.name, league.name)
        assertEquals(expectedLeague.allowDraws, league.allowDraws)
        assertEquals(expectedLeague.maxScore, league.maxScore)
    }

    @Test
    internal fun `Should return null when league does not exist`() {
        // given
        val query = GetLeagueQuery(UUID.randomUUID())
        every { repository.byId(query.id) } returns null
        // when
        val league = handler.handle(query)
        // then
        verify(exactly = 1) { repository.byId(query.id) }
        assertNull(league)
    }
}