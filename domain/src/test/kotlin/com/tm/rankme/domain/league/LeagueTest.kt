package com.tm.rankme.domain.league

import com.tm.rankme.domain.leagueName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class LeagueTest {

    @Test
    internal fun `Should create league with default params`() {
        // when
        val league = League(leagueName)
        // then
        assertNull(league.id)
        assertEquals(leagueName, league.name)
        assertNotNull(league.settings)
        assertEquals(false, league.settings.allowDraws)
        assertEquals(2, league.settings.maxScore)
    }

    @Test
    internal fun `Should create league with id`() {
        // given
        val id = "league-111"
        // when
        val league = League(id, leagueName)
        // then
        assertEquals(id, league.id)
        assertEquals(leagueName, league.name)
    }

    @Test
    internal fun `Should change allow draws setting`() {
        // given
        val league = League(leagueName)
        // when
        league.setAllowDraws(true)
        // then
        assertTrue(league.settings.allowDraws)
    }

    @Test
    internal fun `Should change max score setting`() {
        // given
        val league = League(leagueName)
        // when
        league.setMaxScore(8)
        // then
        assertEquals(8, league.settings.maxScore)
    }
}