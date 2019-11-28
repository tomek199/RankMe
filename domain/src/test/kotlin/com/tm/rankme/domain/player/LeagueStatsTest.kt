package com.tm.rankme.domain.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class LeagueStatsTest {
    @Test
    internal fun `should create LeagueStats with default parameters`() {
        // given
        val leagueId = "league-11111"
        // when
        val leagueStats = LeagueStats(leagueId)
        // then
        assertEquals(leagueId, leagueStats.leagueId)
        assertEquals(1500, leagueStats.rating)
        assertEquals(350, leagueStats.deviation)
        assertEquals(0, leagueStats.won)
        assertEquals(0, leagueStats.lost)
        assertEquals(0, leagueStats.draw)
        assertNull(leagueStats.lastMatch)
    }

    @Test
    internal fun `should return default rating deviation`() {
        // given
        val leagueStats = LeagueStats("league-11111")
        val lastMatch = LocalDate.now().minusWeeks(3)
        // when
        leagueStats.lastMatch = lastMatch
        // then
        assertEquals(350, leagueStats.deviation)
    }

    @Test
    internal fun `should return correct rating deviation when player played game in last period`() {
        // given
        val leagueStats = LeagueStats("league-11111")
        // when
        leagueStats.lastMatch = LocalDate.now().minusDays(6)
        leagueStats.deviation = 224
        // then
        assertEquals(229, leagueStats.deviation)

    }

    @Test
    internal fun `should return bigger rating deviation after inactivity period`() {
        // given
        val leagueStats = LeagueStats("league-11111")
        // when
        leagueStats.lastMatch = LocalDate.now().minusWeeks(3)
        leagueStats.deviation = 158
        // then
        assertEquals(185, leagueStats.deviation)
    }
}