package com.tm.rankme.application.competitor

import com.tm.rankme.application.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
internal class CompetitorMutationTest {
    private val repository = Mockito.mock(CompetitorRepository::class.java)
    private val mapper: Mapper<Competitor, CompetitorModel> = CompetitorMapper()
    private val mutation = CompetitorMutation(repository, mapper)

    private val leagueId = "l-111"
    private val username = "Optimus Prime"


    @BeforeEach
    internal fun setUp() {
        given(repository.save(any(Competitor::class.java)))
                .willReturn(Competitor(leagueId, "c-111", username, Statistics()))
    }

    @Test
    internal fun `Should add competitor with default params`() {
        // given
        val expectedStatistics = Statistics()
        // when
        val competitor = mutation.addCompetitor(leagueId, username)
        // then
        assertNotNull(competitor.id)
        assertEquals(leagueId, competitor.leagueId)
        assertEquals(username, competitor.username)
        assertEquals(expectedStatistics.deviation, competitor.statistics.deviation)
        assertEquals(expectedStatistics.rating, competitor.statistics.rating)
        assertNull(competitor.statistics.lastGame) // fixme
        assertEquals(expectedStatistics.won, competitor.statistics.won)
        assertEquals(expectedStatistics.lost, competitor.statistics.lost)
        assertEquals(expectedStatistics.draw, competitor.statistics.draw)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}