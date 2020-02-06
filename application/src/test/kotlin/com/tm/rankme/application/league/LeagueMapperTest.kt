package com.tm.rankme.application.league

import com.tm.rankme.application.Mapper
import com.tm.rankme.domain.league.League
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LeagueMapperTest {
    private val leagueName = "Star Wars"
    private val leagueId = "l-111"
    private val mapper: Mapper<League, LeagueModel> = LeagueMapper()

    @Test
    internal fun `Should map model to domain`() {
        // given
        val model = LeagueModel(leagueId, leagueName, LeagueSettingsModel(true, 5))
        // when
        val domain = mapper.toDomain(model)
        // then
        assertEquals(model.id, domain.id)
        assertEquals(model.name, domain.name)
        assertEquals(model.settings.allowDraws, domain.settings.allowDraws)
        assertEquals(model.settings.maxScore, domain.settings.maxScore)
    }

    @Test
    internal fun `Should map domain to model`() {
        // given
        val domain = League(leagueId, leagueName)
        domain.setAllowDraws(true)
        domain.setMaxScore(7)
        // when
        val model = mapper.toModel(domain)
        // then
        assertEquals(domain.id, model.id)
        assertEquals(domain.name, model.name)
        assertEquals(domain.settings.allowDraws, model.settings.allowDraws)
        assertEquals(domain.settings.maxScore, model.settings.maxScore)
    }

    @Test
    internal fun `Should throw IllegalStateException when domain league id is null`() {
        // when
        val domain = League(leagueName)
        // then
        assertFailsWith<IllegalStateException> { mapper.toModel(domain) }
    }
}