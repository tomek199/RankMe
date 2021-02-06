package com.tm.rankme.domain.game

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class ResultTest {
    @Test
    internal fun `Should create result`() {
        // given
        val score = 2
        val deviationDelta = -45
        val ratingDelta = 38
        // when
        val result = Result(score, deviationDelta, ratingDelta)
        // then
        assertEquals(score, result.score)
        assertEquals(deviationDelta, result.deviationDelta)
        assertEquals(ratingDelta, result.ratingDelta)
    }
}