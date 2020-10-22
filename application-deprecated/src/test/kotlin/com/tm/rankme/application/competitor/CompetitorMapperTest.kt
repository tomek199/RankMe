package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
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
        val domain = Competitor(leagueId, competitorId, competitorUsername, 154, 2564, lastGame)
        // when
        val model = mapper.toModel(domain)
        // then
        assertEquals(domain.id, model.id)
        assertEquals(domain.username, model.username)
        assertEquals(domain.deviation, model.deviation)
        assertEquals(domain.rating, model.rating)
        assertEquals(domain.lastGame, model.lastGame)
    }

    @Test
    internal fun `Should throw IllegalStateException when domain competitor id is null`() {
        // given
        val domain = Competitor(leagueId, competitorUsername)
        // when
        val exception = assertFailsWith<IllegalStateException> { mapper.toModel(domain) }
        // then
        assertEquals("Competitor id can't be null!", exception.message)
    }
}