package com.tm.rankme.application.competitor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.time.LocalDate
import kotlin.test.assertFailsWith

internal class CompetitorQueryTest {
    private val competitorService: CompetitorService = Mockito.mock(CompetitorService::class.java)
    private val query = CompetitorQuery(competitorService)
    private val id = "comp-1"

    @Test
    internal fun `Should return competitor by id`() {
        val username = "Optimus Prime"
        // given
        val statisticsModel = CompetitorStatisticsModel(145, 2359, 35, 34, 33, LocalDate.now())
        given(competitorService.get(id)).willReturn(CompetitorModel(id, username, statisticsModel))
        // when
        val competitor: CompetitorModel? = query.competitor(id)
        // then
        assertEquals(id, competitor!!.id)
        assertEquals(username, competitor.username)
    }

    @Test
    internal fun `Should throw IllegalStateException when competitor is not found`() {
        // given
        given(competitorService.get(id)).willThrow(IllegalStateException::class.java)
        // then
        assertFailsWith<IllegalStateException> { query.competitor(id) }
    }

    @Test
    internal fun `Should return competitors list by league id`() {
        // given
        val leagueId = "league-1"
        val statistics1 = CompetitorStatisticsModel(250, 1500, 0, 0, 0, LocalDate.now())
        val competitor1 = CompetitorModel( "comp-1", "Optimus Prime", statistics1)
        val statistics2 = CompetitorStatisticsModel(250, 1500, 0, 0, 0, LocalDate.now())
        val competitor2 = CompetitorModel("comp-2", "Megatron", statistics2)
        given(competitorService.getListForLeague(leagueId)).willReturn(listOf(competitor1, competitor2))
        // when
        val competitors: List<CompetitorModel> = query.competitorsByLeagueId(leagueId)
        // then
        assertEquals(2, competitors.size)
        assertEquals(competitor1.id, competitors[0].id)
        assertEquals(competitor1.username, competitors[0].username)
        assertEquals(competitor2.id, competitors[1].id)
        assertEquals(competitor2.username, competitors[1].username)
    }

    @Test
    internal fun `Should return empty list when competitors are not found`() {
        // given
        val leagueId = "league-1"
        given(competitorService.getListForLeague(leagueId)).willReturn(emptyList())
        // when
        val competitors: List<CompetitorModel> = query.competitorsByLeagueId(leagueId)
        // then
        assertTrue(competitors.isEmpty())
    }
}
