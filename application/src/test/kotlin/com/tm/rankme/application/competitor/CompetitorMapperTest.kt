package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class CompetitorMapperTest {
    private val leagueId = "league-1"
    private val competitorId = "comp-1"
    private val competitorUsername = "Optimus Prime"
    private val mapper: Mapper<Competitor, CompetitorModel> = CompetitorMapper()

    @Test
    internal fun `Should map domain to model`() {
        // given
        val lastGame = LocalDate.of(2020, 3, 30)
        val statistics = Statistics(154, 2564, 53, 34, 95, lastGame)
        val domain = Competitor(leagueId, competitorId, competitorUsername, statistics)
        // when
        val model = mapper.toModel(domain)
        // then
        assertEquals(domain.id, model.id)
        assertEquals(domain.username, model.username)
        assertEquals(domain.leagueId, model.leagueId)
        assertEquals(domain.statistics.deviation, model.statistics.deviation)
        assertEquals(domain.statistics.rating, model.statistics.rating)
        assertEquals(domain.statistics.won, model.statistics.won)
        assertEquals(domain.statistics.lost, model.statistics.lost)
        assertEquals(domain.statistics.draw, model.statistics.draw)
        assertEquals(domain.statistics.lastGame, model.statistics.lastGame)
    }

    @Test
    internal fun `Should throw IllegalStateException when domain competitor id is null`() {
        // when
        val domain = Competitor(leagueId, competitorUsername, Statistics())
        // then
        assertFailsWith<IllegalStateException> { mapper.toModel(domain) }
    }
}