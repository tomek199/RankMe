package com.tm.rankme.application.competitor

import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class CompetitorMutationTest {
    private val competitorService: CompetitorService = Mockito.mock(CompetitorService::class.java)
    private val mutation = CompetitorMutation(competitorService)

    private val leagueId = "league-1"
    private val username = "Optimus Prime"

    @BeforeEach
    internal fun setUp() {
        val statistics = CompetitorStatisticsModel(350, 1500, 0, 0, 0, null)
        given(competitorService.create(leagueId, username))
            .willReturn(CompetitorModel("comp-1", username, statistics))
    }

    @Test
    internal fun `Should add competitor with default params`() {
        // given
        val expectedStatistics = Statistics()
        val input = AddCompetitorInput(leagueId, username)
        // when
        val competitor: CompetitorModel = mutation.addCompetitor(input)
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
}
