package com.tm.rankme.domain.game

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PlayerTest {
    @Test
    internal fun `should update deviation, rating and rating delta`() {
        // given
        val newDeviation = 239
        val newRating = 1821
        val player = Player("c-111", "Optimus Prime", 248, 1764)
        // when
        player.update(newDeviation, newRating)
        // then
        assertEquals(newDeviation, player.deviation)
        assertEquals(newRating, player.rating)
        assertEquals(57, player.ratingDelta)
    }
}