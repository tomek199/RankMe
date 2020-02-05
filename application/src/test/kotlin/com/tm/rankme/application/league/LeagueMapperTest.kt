package com.tm.rankme.application.league

import com.tm.rankme.application.Mapper
import com.tm.rankme.domain.league.League
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LeagueMapperTest {
    private val mapper: Mapper<League, LeagueModel> = LeagueMapper()

    @Test
    fun `should map model to domain`() {
        // given
        val model = LeagueModel("l-111", "Star Wars", LeagueSettingsModel(true, 5))
        // when
        val domain = mapper.toDomain(model)
        // then
        assertEquals(model.id, domain.id)
        assertEquals(model.name, domain.name)
        assertEquals(model.settings.allowDraws, domain.settings.allowDraws)
        assertEquals(model.settings.maxScore, domain.settings.maxScore)
    }

    @Test
    fun `should map domain to model`() {
        // given
        val domain = League("l-111", "Star Wars")
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
    internal fun `should throw IllegalStateException when domain league id is null`() {
        // when
        val domain = League("Star Wars")
        // then
        assertFailsWith<IllegalStateException> { mapper.toModel(domain) }
    }
}