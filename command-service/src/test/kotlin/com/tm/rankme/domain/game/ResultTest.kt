package com.tm.rankme.domain.game

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ResultTest {
    @Test
    internal fun `Should create result`() {
        // given
        val score = 2
        val deviation = 318
        val deviationDelta = -45
        val rating = 1286
        val ratingDelta = 38
        // when
        val result = Result(score, deviation, deviationDelta, rating, ratingDelta)
        // then
        assertEquals(score, result.score)
        assertEquals(deviation, result.deviation)
        assertEquals(deviationDelta, result.deviationDelta)
        assertEquals(rating, result.rating)
        assertEquals(ratingDelta, result.ratingDelta)
    }
}