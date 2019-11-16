package com.elorating.domain.league

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class LeagueTest {
    @Test
    internal fun `should create league with default params`() {
        // given
        val name = "Tennis masters"
        // when
        val league = League(name)
        assertEquals(name, league.name)
        // then
        assertNull(league.id)
        assertNotNull(league.settings)
        assertEquals(false, league.settings.allowDraws)
        assertEquals(2, league.settings.maxScore)
    }

    @Test
    internal fun `should create league with id`() {
        // given
        val id = "11111"
        val name = "Tennis masters"
        // when
        val league = League(id, name)
        // then
        assertEquals(id, league.id)
        assertEquals(name, league.name)
    }

    @Test
    internal fun `should change allow draws setting`() {
        // given
        val league = League("Tennis masters")
        // when
        league.setAllowDraws(true)
        // then
        assertTrue(league.settings.allowDraws)
    }

    @Test
    internal fun `should change max score setting`() {
        // given
        val league = League("Tennis masters")
        // when
        league.setMaxScore(8)
        // then
        assertEquals(8, league.settings.maxScore)
    }
}