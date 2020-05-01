package com.tm.rankme.domain.competitor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class StatisticsTest {
    @Test
    internal fun `Should create Statistics with default parameters`() {
        // when
        val statistics = Statistics()
        // then
        assertEquals(350, statistics.deviation)
        assertEquals(1500, statistics.rating)
        assertEquals(0, statistics.won)
        assertEquals(0, statistics.lost)
        assertEquals(0, statistics.draw)
        assertNull(statistics.lastGame)
    }

    @Test
    internal fun `Should create Statistics with custom parameters`() {
        // given
        val expectedDeviation = 186
        val expectedRating = 1343
        val expectedWon = 46
        val expectedLost = 34
        val expectedDraw = 12
        val expectedLastGame = LocalDate.now()
        // when
        val statistics = Statistics(
            expectedDeviation, expectedRating,
            expectedWon, expectedLost, expectedDraw, expectedLastGame
        )
        // then
        assertEquals(192, statistics.deviation) // Deviation will be always higher than initialized value
        assertEquals(expectedRating, statistics.rating)
        assertEquals(expectedWon, statistics.won)
        assertEquals(expectedLost, statistics.lost)
        assertEquals(expectedDraw, statistics.draw)
        assertEquals(expectedLastGame, statistics.lastGame)
    }

    @Test
    internal fun `Should return default rating deviation`() {
        // given
        val statistics = Statistics()
        val lastGame = LocalDate.now().minusWeeks(3)
        // when
        statistics.lastGame = lastGame
        // then
        assertEquals(350, statistics.deviation)
    }

    @Test
    internal fun `Should return correct rating deviation when competitor played game in last period`() {
        // given
        val statistics = Statistics()
        // when
        statistics.lastGame = LocalDate.now().minusDays(6)
        statistics.deviation = 224
        // then
        assertEquals(229, statistics.deviation)

    }

    @Test
    internal fun `Should return bigger rating deviation after inactivity period`() {
        // given
        val statistics = Statistics()
        // when
        statistics.lastGame = LocalDate.now().minusWeeks(3)
        statistics.deviation = 158
        // then
        assertEquals(185, statistics.deviation)
    }

    @Test
    internal fun `Should add draw game`() {
        // given
        val statistics = Statistics()
        // when
        statistics.addGame(3, 3)
        // then
        assertEquals(1, statistics.draw)
        assertEquals(0, statistics.won)
        assertEquals(0, statistics.lost)
    }

    @Test
    internal fun `Should add won game`() {
        // given
        val statistics = Statistics()
        // when
        statistics.addGame(3, 2)
        // then
        assertEquals(0, statistics.draw)
        assertEquals(1, statistics.won)
        assertEquals(0, statistics.lost)
    }

    @Test
    internal fun `Should add lost game`() {
        // given
        val statistics = Statistics()
        // when
        statistics.addGame(2, 3)
        // then
        assertEquals(0, statistics.draw)
        assertEquals(0, statistics.won)
        assertEquals(1, statistics.lost)
    }
}