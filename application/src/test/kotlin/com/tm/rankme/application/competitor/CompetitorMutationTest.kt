package com.tm.rankme.application.competitor

import com.tm.rankme.application.any
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class CompetitorMutationTest {
    private val competitorService: CompetitorService = Mockito.mock(CompetitorService::class.java)
    private val leagueRepository = Mockito.mock(LeagueRepository::class.java)
    private val mapper: Mapper<Competitor, CompetitorModel> = CompetitorMapper()
    private val mutation = CompetitorMutation(competitorService, leagueRepository, mapper)

    private val leagueId = "league-1"
    private val username = "Optimus Prime"

    @BeforeEach
    internal fun setUp() {
        given(competitorService.saveCompetitor(any(Competitor::class.java)))
            .willReturn(Competitor(leagueId, "comp-1", username, Statistics()))
    }

    @Test
    internal fun `Should add competitor with default params`() {
        // given
        given(leagueRepository.findById(leagueId)).willReturn(League(leagueId, "Transformers"))
        val expectedStatistics = Statistics()
        val input = AddCompetitorInput(leagueId, username)
        // when
        val competitor = mutation.addCompetitor(input)
        // then
        assertNotNull(competitor.id)
        assertEquals(username, competitor.username)
        assertEquals(expectedStatistics.deviation, competitor.statistics.deviation)
        assertEquals(expectedStatistics.rating, competitor.statistics.rating)
        assertNull(competitor.statistics.lastGame)
        assertEquals(expectedStatistics.won, competitor.statistics.won)
        assertEquals(expectedStatistics.lost, competitor.statistics.lost)
        assertEquals(expectedStatistics.draw, competitor.statistics.draw)
    }

    @Test
    internal fun `Should throw exception when league does not exist`() {
        // given
        given(leagueRepository.findById(leagueId)).willReturn(null)
        val input = AddCompetitorInput(leagueId, username)
        // when
        val exception = assertFailsWith<IllegalStateException> { mutation.addCompetitor(input) }
        // then
        assertEquals("League does not exist!", exception.message)
    }
}
