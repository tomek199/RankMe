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
import org.mockito.Mockito.verify
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class CompetitorServiceTest {
    private val repository = mock(CompetitorRepository::class.java)
    private val service: CompetitorService = CompetitorServiceImpl(repository)
    private val competitorId = "comp-1"
    private val leagueId = "league-1"

    @Test
    internal fun `Should return competitor`() {
        // given
        val expectedCompetitor = Competitor(leagueId, competitorId, "Optimus Prime", Statistics())
        given(repository.findById(competitorId)).willReturn(expectedCompetitor)
        // when
        val competitor = service.getCompetitor(competitorId, leagueId)
        // then
        assertEquals(expectedCompetitor.id, competitor.id)
        assertEquals(expectedCompetitor.leagueId, competitor.leagueId)
        assertEquals(expectedCompetitor.username, competitor.username)
        assertEquals(expectedCompetitor.statistics, competitor.statistics)
    }

    @Test
    internal fun `Should throw IllegalStateException when competitor is not found`() {
        // given
        given(repository.findById(competitorId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            service.getCompetitor(competitorId, leagueId)
        }
        // then
        assertEquals("Competitor $competitorId is not found", exception.message)
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
            service.getCompetitor(competitorId, leagueId)
        }
        // then
        assertEquals("Competitor $competitorId is not assigned to league $leagueId", exception.message)
    }

    @Test
    internal fun `Should update competitors statistic`() {
        // given
        val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", Statistics())
        val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", Statistics())
        val game = GameFactory.create(firstCompetitor, 2, secondCompetitor, 1, leagueId)
        // when
        service.updateCompetitorsStatistic(firstCompetitor, secondCompetitor, game)
        // then
        verify(repository, Mockito.times(2)).save(any(Competitor::class.java))
    }
}