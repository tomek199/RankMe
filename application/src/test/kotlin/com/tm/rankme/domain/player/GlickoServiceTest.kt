package com.tm.rankme.domain.player

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class GlickoServiceTest {
    @Test
    internal fun `Should return correct result for new players when draw`() {
        // when
        val glicko = GlickoService(350, 1500, 1, 350, 1500, 1)
        // then
        assertEquals(1500, glicko.playerOneRating)
        assertEquals(1500, glicko.playerTwoRating)
        assertEquals(290, glicko.playerOneDeviation)
        assertEquals(290, glicko.playerTwoDeviation)
    }

    // Deviation for players provided are already recalculated based on last game played.

    @Test
    internal fun `Should return correct result for players when draw`() {
        // when
        val glicko = GlickoService(229, 2145, 1, 318, 1839, 1)
        // then
        assertEquals(214, glicko.playerOneDeviation)
        assertEquals(2094, glicko.playerOneRating)
        assertEquals(274, glicko.playerTwoDeviation)
        assertEquals(1946, glicko.playerTwoRating)
    }

    @Test
    internal fun `Should return correct result when player one won`() {
        // when
        val glicko = GlickoService(171, 2493, 2, 185, 2435, 1)
        // then
        assertEquals(158, glicko.playerOneDeviation)
        assertEquals(2546, glicko.playerOneRating)
        assertEquals(168, glicko.playerTwoDeviation)
        assertEquals(2374, glicko.playerTwoRating)
    }

    @Test
    internal fun `Should return correct result when player two won`() {
        // when
        val glicko = GlickoService(171, 2493, 0, 185, 2435, 1)
        // then
        assertEquals(158, glicko.playerOneDeviation)
        assertEquals(2422, glicko.playerOneRating)
        assertEquals(168, glicko.playerTwoDeviation)
        assertEquals(2517, glicko.playerTwoRating)
    }
}