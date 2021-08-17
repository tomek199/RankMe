package com.tm.rankme.domain.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class PlayerPlayedGameTest {
    @Test
    internal fun `Should create event`() {
        // given
        val deviationDelta = -76
        val ratingDelta = 142
        val score = 3
        val aggregateId = randomNanoId()
        val version = 3L
        // when
        val event = PlayerPlayedGame(deviationDelta, ratingDelta, score, aggregateId, version)
        // then
        assertEquals("player-played-game", event.type)
        assertEquals(deviationDelta, event.deviationDelta)
        assertEquals(version, event.version)
        assertNotNull(event.timestamp)
        assertEquals(aggregateId, event.aggregateId)
        assertEquals(ratingDelta, event.ratingDelta)
        assertEquals(score, event.score)
    }
}