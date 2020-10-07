package com.tm.rankme.application.competitor

import org.dataloader.stats.Statistics
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
        given(competitorService.create(leagueId, username))
            .willReturn(CompetitorModel("comp-1", username, 350, 1500, null))
    }

    @Test
    internal fun `Should add competitor with default params`() {
        // given
        val input = AddCompetitorInput(leagueId, username)
        // when
        val competitor: CompetitorModel = mutation.addCompetitor(input)
        // then
        assertNotNull(competitor.id)
        assertEquals(username, competitor.username)
        assertEquals(350, competitor.deviation)
        assertEquals(1500, competitor.rating)
        assertNull(competitor.lastGame)
    }
}
