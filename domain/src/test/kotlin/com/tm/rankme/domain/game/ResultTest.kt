package com.tm.rankme.domain.game

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ResultTest {
    @Test
    internal fun `Should create result`() {
        // given
        val score = 2
        val ratingDelta = -24
        // when
        val result = Result(score, ratingDelta)
        // then
        assertEquals(score, result.score)
        assertEquals(ratingDelta, result.ratingDelta)
    }
}