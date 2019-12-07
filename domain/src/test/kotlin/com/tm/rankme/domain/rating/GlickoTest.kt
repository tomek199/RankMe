package com.tm.rankme.domain.rating

import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class GlickoTest {
    @Test
    internal fun `should return correct result for new players when draw`() {
        // given
        val playerOneStats = Statistics()
        val playerTwoStats = Statistics()
        // when
        val glicko = Glicko(playerOneStats, playerTwoStats, Pair(2, 2))
        // then
        assertEquals(1500, glicko.playerOneRating())
        assertEquals(1500, glicko.playerTwoRating())
        assertEquals(290, glicko.playerOneDeviation())
        assertEquals(290, glicko.playerTwoDeviation())
    }

    @Test
    internal fun `should return correct result for players when draw`() {
        // given
        val playerOneStats = Statistics()
        val playerTwoStats = Statistics()
        playerOneStats.lastGame = LocalDate.now()
        playerOneStats.deviation = 224
        playerOneStats.rating = 2145
        playerTwoStats.lastGame = LocalDate.now()
        playerTwoStats.deviation = 314
        playerTwoStats.rating = 1839
        // when
        val glicko = Glicko(playerOneStats, playerTwoStats, Pair(1, 1))
        // then
        assertEquals(214, glicko.playerOneDeviation())
        assertEquals(2094, glicko.playerOneRating())
        assertEquals(274, glicko.playerTwoDeviation())
        assertEquals(1946, glicko.playerTwoRating())
    }

    @Test
    internal fun `should return correct result for when player one won`() {
        // given
        val playerOneStats = Statistics()
        val playerTwoStats = Statistics()
        playerOneStats.lastGame = LocalDate.now()
        playerOneStats.deviation = 164
        playerOneStats.rating = 2493
        playerTwoStats.lastGame = LocalDate.now()
        playerTwoStats.deviation = 179
        playerTwoStats.rating = 2435
        // when
        val glicko = Glicko(playerOneStats, playerTwoStats, Pair(2, 1))
        // then
        assertEquals(158, glicko.playerOneDeviation())
        assertEquals(2546, glicko.playerOneRating())
        assertEquals(168, glicko.playerTwoDeviation())
        assertEquals(2374, glicko.playerTwoRating())
    }

    @Test
    internal fun `should return correct result for when player two won`() {
        // given
        val playerOneStats = Statistics()
        val playerTwoStats = Statistics()
        playerOneStats.lastGame = LocalDate.now()
        playerOneStats.deviation = 164
        playerOneStats.rating = 2493
        playerTwoStats.lastGame = LocalDate.now()
        playerTwoStats.deviation = 179
        playerTwoStats.rating = 2435
        // when
        val glicko = Glicko(playerOneStats, playerTwoStats, Pair(0, 1))
        // then
        assertEquals(158, glicko.playerOneDeviation())
        assertEquals(2422, glicko.playerOneRating())
        assertEquals(168, glicko.playerTwoDeviation())
        assertEquals(2517, glicko.playerTwoRating())
    }
}