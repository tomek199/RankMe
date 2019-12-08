package com.tm.rankme.domain.competitor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class StatisticsTest {
    @Test
    internal fun `should create Statistics with default parameters`() {
        // when
        val leagueStats = Statistics()
        // then
        assertEquals(350, leagueStats.deviation)
        assertEquals(1500, leagueStats.rating)
        assertEquals(0, leagueStats.won)
        assertEquals(0, leagueStats.lost)
        assertEquals(0, leagueStats.draw)
        assertNull(leagueStats.lastGame)
    }

    @Test
    internal fun `should create Statistics with custom parameters`() {
        // given
        val expectedDeviation = 186
        val expectedRating = 1343
        val expectedWon = 46
        val expectedLost = 34
        val expectedDraw = 12
        val expectedLastGame = LocalDate.now()
        // when
        val statistics = Statistics(expectedDeviation, expectedRating,
                expectedWon, expectedLost, expectedDraw, expectedLastGame)
        // then
        assertEquals(192, statistics.deviation) // Deviation will be always higher than initialized value
        assertEquals(expectedRating, statistics.rating)
        assertEquals(expectedWon, statistics.won)
        assertEquals(expectedLost, statistics.lost)
        assertEquals(expectedDraw, statistics.draw)
        assertEquals(expectedLastGame, statistics.lastGame)
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
    internal fun `should return correct rating deviation when competitor played game in last period`() {
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