package com.tm.rankme.domain.game

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class GlickoServiceTest {
    private val playerOne = Player("comp-1", "Darth Vader", 350, 1500)
    private val playerTwo = Player("comp-2", "Han Solo", 350, 1500)

    @Test
    internal fun `Should throw exception when first player score is not provided`() {
        // given
        playerTwo.score = 1
        // when
        val exception = assertFailsWith<IllegalArgumentException> { GlickoService(playerOne, playerTwo) }
        // then
        assertEquals("Player one score is not provided!", exception.message)
    }

    @Test
    internal fun `Should throw exception when second player score is not provided`() {
        // given
        playerOne.score = 1
        // when
        val exception = assertFailsWith<IllegalArgumentException> { GlickoService(playerOne, playerTwo) }
        // then
        assertEquals("Player two score is not provided!", exception.message)
    }

    @Test
    internal fun `Should return correct result for new players when draw`() {
        // given
        playerOne.score = 1
        playerTwo.score = 1
        // when
        val glicko = GlickoService(playerOne, playerTwo)
        // then
        assertEquals(1500, glicko.playerOneRating)
        assertEquals(1500, glicko.playerTwoRating)
        assertEquals(290, glicko.playerOneDeviation)
        assertEquals(290, glicko.playerTwoDeviation)
    }

    // Deviation for players provided are already recalculated based on last game played.

    @Test
    internal fun `Should return correct result for players when draw`() {
        // given
        playerOne.score = 1
        playerOne.deviation = 229
        playerOne.rating = 2145
        playerTwo.score = 1
        playerTwo.deviation = 318
        playerTwo.rating = 1839
        // when
        val glicko = GlickoService(playerOne, playerTwo)
        // then
        assertEquals(214, glicko.playerOneDeviation)
        assertEquals(2094, glicko.playerOneRating)
        assertEquals(274, glicko.playerTwoDeviation)
        assertEquals(1946, glicko.playerTwoRating)
    }

    @Test
    internal fun `Should return correct result when player one won`() {
        // given
        playerOne.score = 2
        playerOne.deviation = 171
        playerOne.rating = 2493
        playerTwo.score = 1
        playerTwo.deviation = 185
        playerTwo.rating = 2435
        // when
        val glicko = GlickoService(playerOne, playerTwo)
        // then
        assertEquals(158, glicko.playerOneDeviation)
        assertEquals(2546, glicko.playerOneRating)
        assertEquals(168, glicko.playerTwoDeviation)
        assertEquals(2374, glicko.playerTwoRating)
    }

    @Test
    internal fun `Should return correct result when player two won`() {
        // given
        playerOne.score = 0
        playerOne.deviation = 171
        playerOne.rating = 2493
        playerTwo.score = 1
        playerTwo.deviation = 185
        playerTwo.rating = 2435
        // when
        val glicko = GlickoService(playerOne, playerTwo)
        // then
        assertEquals(158, glicko.playerOneDeviation)
        assertEquals(2422, glicko.playerOneRating)
        assertEquals(168, glicko.playerTwoDeviation)
        assertEquals(2517, glicko.playerTwoRating)
    }
}