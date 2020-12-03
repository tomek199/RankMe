package com.tm.rankme.domain.league

import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class LeagueRenamedTest {
    @Test
    internal fun `Should create event`() {
        // given
        val aggregateId = UUID.randomUUID()
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