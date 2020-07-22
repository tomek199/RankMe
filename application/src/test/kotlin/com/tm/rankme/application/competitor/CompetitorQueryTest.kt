package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

internal class CompetitorQueryTest {
    private val repository: CompetitorRepository = Mockito.mock(CompetitorRepository::class.java)
    private val mapper: Mapper<Competitor, CompetitorModel> = CompetitorMapper()
    private val query = CompetitorQuery(repository, mapper)
    private val id = "comp-1"

    @Test
    internal fun `Should return competitor by id`() {
        val leagueId = "league-1"
        val username = "Optimus Prime"
        // given
        given(repository.findById(id)).willReturn(Competitor(leagueId, id, username, Statistics()))
        // when
        val competitor = query.competitor(id)
        // then
        assertEquals(id, competitor!!.id)
        assertEquals(username, competitor.username)
    }

    @Test
    internal fun `Should return null when competitor is not found`() {
        // given
        given(repository.findById(id)).willReturn(null)
        // when
        val competitor = query.competitor(id)
        // then
        assertNull(competitor)
    }

    @Test
    internal fun `Should return competitors list by league id`() {
        // given
        val leagueId = "league-1"
        val competitor1 = Competitor(leagueId, "comp-1", "Optimus Prime", Statistics())
        val competitor2 = Competitor(leagueId, "comp-2", "Megatron", Statistics())
        given(repository.findByLeagueId(leagueId)).willReturn(listOf(competitor1, competitor2))
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
        given(repository.findByLeagueId(leagueId)).willReturn(emptyList())
        // when
        val competitors = query.competitorsByLeagueId(leagueId)
        // then
        assertTrue(competitors.isEmpty())
    }
}
