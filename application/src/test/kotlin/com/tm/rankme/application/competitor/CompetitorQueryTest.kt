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
        given(competitorService.get(id)).willReturn(CompetitorModel(id, username, 145, 2359, LocalDate.now()))
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
        val lastGameDate = LocalDate.now()
        val competitor1 = CompetitorModel( "comp-1", "Optimus Prime", 350, 1500, lastGameDate)
        val competitor2 = CompetitorModel("comp-2", "Megatron", 350, 1500, lastGameDate)
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
