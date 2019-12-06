package com.tm.rankme.domain.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class StatisticsTest {
    @Test
    internal fun `should create LeagueStats with default parameters`() {
        // when
        val leagueStats = Statistics()
        // then
        assertEquals(1500, leagueStats.rating)
        assertEquals(350, leagueStats.deviation)
        assertEquals(0, leagueStats.won)
        assertEquals(0, leagueStats.lost)
        assertEquals(0, leagueStats.draw)
        assertNull(leagueStats.lastGame)
    }

    @Test
    internal fun `should return default rating deviation`() {
        // given
        val leagueStats = Statistics()
        val lastMatch = LocalDate.now().minusWeeks(3)
        // when
        leagueStats.lastGame = lastMatch
        // then
        assertEquals(350, leagueStats.deviation)
    }

    @Test
    internal fun `should return correct rating deviation when player played game in last period`() {
        // given
        val leagueStats = Statistics()
        // when
        leagueStats.lastGame = LocalDate.now().minusDays(6)
        leagueStats.deviation = 224
        // then
        assertEquals(229, leagueStats.deviation)

    }

    @Test
    internal fun `should return bigger rating deviation after inactivity period`() {
        // given
        val leagueStats = Statistics()
        // when
        leagueStats.lastGame = LocalDate.now().minusWeeks(3)
        leagueStats.deviation = 158
        // then
        assertEquals(185, leagueStats.deviation)
    }
}