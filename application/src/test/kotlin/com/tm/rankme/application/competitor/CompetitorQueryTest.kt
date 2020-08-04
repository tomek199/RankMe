package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import kotlin.test.assertFailsWith

internal class CompetitorQueryTest {
    private val competitorService: CompetitorService = Mockito.mock(CompetitorService::class.java)
    private val mapper: Mapper<Competitor, CompetitorModel> = CompetitorMapper()
    private val query = CompetitorQuery(competitorService, mapper)
    private val id = "comp-1"

    @Test
    internal fun `Should return competitor by id`() {
        val leagueId = "league-1"
        val username = "Optimus Prime"
        // given
        given(competitorService.getCompetitor(id)).willReturn(Competitor(leagueId, id, username, Statistics()))
        // when
        val competitor = query.competitor(id)
        // then
        assertEquals(id, competitor!!.id)
        assertEquals(username, competitor.username)
    }

    @Test
    internal fun `Should throw IllegalStateException when competitor is not found`() {
        // given
        given(competitorService.getCompetitor(id)).willThrow(IllegalStateException::class.java)
        // then
        assertFailsWith<IllegalStateException> { query.competitor(id) }
    }

    @Test
    internal fun `Should return competitors list by league id`() {
        // given
        val leagueId = "league-1"
        val competitor1 = Competitor(leagueId, "comp-1", "Optimus Prime", Statistics())
        val competitor2 = Competitor(leagueId, "comp-2", "Megatron", Statistics())
        given(competitorService.getCompetitors(leagueId)).willReturn(listOf(competitor1, competitor2))
        // when
        val competitors = query.competitorsByLeagueId(leagueId)
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
        given(competitorService.getCompetitors(leagueId)).willReturn(emptyList())
        // when
        val competitors = query.competitorsByLeagueId(leagueId)
        // then
        assertTrue(competitors.isEmpty())
    }
}
