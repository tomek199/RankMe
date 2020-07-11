package com.tm.rankme.domain.game

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PlayerTest {
    @Test
    internal fun `Should create player`() {
        // given
        val competitorId = "comp-1"
        val username = "Optimus Prime"
        val deviation = 248
        val rating = 1764
        val score = 3
        val ratingDelta = -74
        // when
        val player = Player(competitorId, username, deviation, rating, score, ratingDelta)
        // then
        assertEquals(competitorId, player.competitorId)
        assertEquals(username, player.username)
        assertEquals(deviation, player.deviation)
        assertEquals(rating, player.rating)
        assertEquals(score, player.score)
        assertEquals(ratingDelta, player.ratingDelta)
    }
}