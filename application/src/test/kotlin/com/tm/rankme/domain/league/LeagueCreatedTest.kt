package com.tm.rankme.domain.league

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class LeagueCreatedTest {
    @Test
    internal fun `Should create event`() {
        // given
        val name = "Star Wars"
        val allowDraws = false
        val maxScore = 2
        // when
        val event = LeagueCreated(name)
        // then
        assertEquals("league-created", event.type)
        assertNotNull(event.aggregateId)
        assertEquals(0, event.version)
        assertNotNull(event.timestamp)
        assertEquals(name, event.name)
        assertEquals(allowDraws, event.allowDraws)
        assertEquals(maxScore, event.maxScore)
    }
}