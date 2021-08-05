package com.tm.rankme.domain.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class LeagueRenamedTest {
    @Test
    internal fun `Should create event`() {
        // given
        val aggregateId = randomNanoId()
        val version = 2L
        val name = "Transformers"
        // when
        val event = LeagueRenamed(aggregateId, version, name)
        // then
        assertEquals("league-renamed", event.type)
        assertEquals(aggregateId, event.aggregateId)
        assertEquals(version, event.version)
        assertNotNull(event.timestamp)
        assertEquals(name, event.name)
    }
}