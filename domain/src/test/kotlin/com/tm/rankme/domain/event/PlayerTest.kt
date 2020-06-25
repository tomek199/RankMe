package com.tm.rankme.domain.event

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PlayerTest {
    @Test
    internal fun `Should create player`() {
        // given
        val id = "player-1"
        val username = "Spiderman"
        val deviation = 234
        val rating = 1483
        // when
        val player = Player(id, username, deviation, rating)
        // then
        assertEquals(id, player.competitorId)
        assertEquals(username, player.username)
        assertEquals(deviation, player.deviation)
        assertEquals(rating, player.rating)
    }
}
