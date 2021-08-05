package com.tm.rankme.domain.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class PlayerCreatedTest {
    @Test
    internal fun `Should create event`() {
        // given
        val leagueId = randomNanoId()
        val name = "Han Solo"
        val deviation = 237
        val rating = 1680
        // when
        val event = PlayerCreated(leagueId, name, deviation, rating)
        // then
        assertEquals("player-created", event.type)
        assertNotNull(event.aggregateId)
        assertEquals(0, event.version)
        assertNotNull(event.timestamp)
        assertEquals(leagueId, event.leagueId)
        assertEquals(name, event.name)
        assertEquals(deviation, event.deviation)
        assertEquals(rating, event.rating)
    }
}