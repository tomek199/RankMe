package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.league.LeagueQuery
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class CompetitorQueryTest {
    private val repository: CompetitorRepository = Mockito.mock(CompetitorRepository::class.java)
    private val mapper: Mapper<Competitor, CompetitorModel> = CompetitorMapper()
    private val query = CompetitorQuery(repository, mapper)
    private val id = "c-111"

    @Test
    internal fun `Should return competitor by id`() {
        val leagueId = "l-111"
        val username = "Optimus Prime"
        // given
        given(repository.findById(id)).willReturn(Competitor(leagueId, id, username, Statistics()))
        // when
        val competitor = query.competitor(id)
        // then
        assertEquals(id, competitor!!.id)
        assertEquals(leagueId, competitor.leagueId)
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
}
