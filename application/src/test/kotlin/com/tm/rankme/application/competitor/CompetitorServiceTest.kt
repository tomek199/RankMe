package com.tm.rankme.application.competitor

import com.tm.rankme.application.any
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.game.GameFactory
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class CompetitorServiceTest {
    private val repository = mock(CompetitorRepository::class.java)
    private val service: CompetitorService = CompetitorServiceImpl(repository)
    private val competitorId = "comp-1"
    private val leagueId = "league-1"

    @Test
    internal fun `Should return competitor for league`() {
        // given
        val expectedCompetitor = Competitor(leagueId, competitorId, "Optimus Prime", Statistics())
        given(repository.findById(competitorId)).willReturn(expectedCompetitor)
        // when
        val competitor = service.getForLeague(competitorId, leagueId)
        // then
        assertEquals(expectedCompetitor.id, competitor.id)
        assertEquals(expectedCompetitor.leagueId, competitor.leagueId)
        assertEquals(expectedCompetitor.username, competitor.username)
        assertEquals(expectedCompetitor.statistics, competitor.statistics)
    }

    @Test
    internal fun `Should return competitor by id`() {
        // given
        val expectedCompetitor = Competitor(leagueId, competitorId, "Optimus Prime", Statistics())
        given(repository.findById(competitorId)).willReturn(expectedCompetitor)
        // when
        val competitor = service.get(competitorId)
        // then
        assertEquals(expectedCompetitor.id, competitor.id)
        assertEquals(expectedCompetitor.leagueId, competitor.leagueId)
        assertEquals(expectedCompetitor.username, competitor.username)
        assertEquals(expectedCompetitor.statistics, competitor.statistics)
    }

    @Test
    internal fun `Should throw IllegalStateException when competitor does not exist`() {
        // given
        given(repository.findById(competitorId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            service.get(competitorId)
        }
        // then
        assertEquals("Competitor $competitorId is not found", exception.message)
    }

    @Test
    internal fun `Should throw IllegalStateException when competitor for league is not found`() {
        // given
        given(repository.findById(competitorId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            service.getForLeague(competitorId, leagueId)
        }
        // then
        assertEquals("Competitor $competitorId is not found", exception.message)
    }

    @Test
    internal fun `Should return competitors list by league id`() {
        // given
        val competitor1 = Competitor(leagueId, "comp-1", "Batman", Statistics())
        val competitor2 = Competitor(leagueId, "comp-2", "Superman", Statistics())
        given(repository.findByLeagueId(leagueId)).willReturn(listOf(competitor1, competitor2))
        // when
        val competitors = service.getListForLeague(leagueId)
        // then
        assertEquals(2, competitors.size)
        assertEquals(competitor1.id, competitors[0].id)
        assertEquals(competitor1.id, competitors[0].id)
        assertEquals(competitor2.username, competitors[1].username)
        assertEquals(competitor2.username, competitors[1].username)
    }

    @Test
    internal fun `Should return empty competitors list by league id`() {
        // given
        given(repository.findByLeagueId(leagueId)).willReturn(emptyList())
        // when
        val competitors = service.getListForLeague(leagueId)
        // then
        assertTrue(competitors.isEmpty())
    }

    @Test
    internal fun `Should throw IllegalStateException when competitor is not assigned to league`() {
        // given
        val competitorId = "comp-1"
        val leagueId = "league-1"
        val competitor = Competitor("other-league-1", competitorId, "Optimus Prime", Statistics())
        given(repository.findById(competitorId)).willReturn(competitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            service.getForLeague(competitorId, leagueId)
        }
        // then
        assertEquals("Competitor $competitorId is not assigned to league $leagueId", exception.message)
    }

    @Test
    internal fun `Should create competitor`() {
        // given
        val expectedCompetitor = Competitor(leagueId, competitorId, "Optimus Prime", Statistics())
        given(repository.save(expectedCompetitor)).willReturn(expectedCompetitor)
        // when
        val competitor = service.create(expectedCompetitor)
        // then
        assertNotNull(competitor.id)
        assertEquals(expectedCompetitor.username, competitor.username)
        assertEquals(expectedCompetitor.statistics.deviation, competitor.statistics.deviation)
        assertEquals(expectedCompetitor.statistics.rating, competitor.statistics.rating)
        assertNull(competitor.statistics.lastGame)
        verify(repository, only()).save(expectedCompetitor)
    }

    @Test
    internal fun `Should update competitors statistic`() {
        // given
        val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", Statistics())
        val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", Statistics())
        val game = GameFactory.create(firstCompetitor, 2, secondCompetitor, 1, leagueId)
        // when
        service.updateStatistic(firstCompetitor, secondCompetitor, game)
        // then
        verify(repository, Mockito.times(2)).save(any(Competitor::class.java))
    }
}