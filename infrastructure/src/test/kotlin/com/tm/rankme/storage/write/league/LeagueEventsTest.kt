package com.tm.rankme.storage.write.league

import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class LeagueEventsTest {
    private val aggregateId: UUID = UUID.randomUUID()
    private val version: Long = 0
    private val timestamp: Long = 12345

    @Test
    internal fun `Should create Create event`() {
        // given
        val type = "league-created"
        val name = "Star Wars"
        val allowDraws = false
        val maxScore = 7
        // when
        val event = Created(type, aggregateId, version, timestamp, name, allowDraws, maxScore)
        // then
        assertEquals(type, event.type)
        assertEquals(aggregateId, event.aggregateId)
        assertEquals(version, event.version)
        assertEquals(timestamp, event.timestamp)
        assertEquals(name, event.name)
        assertEquals(allowDraws, event.allowDraws)
        assertEquals(maxScore, event.maxScore)
    }

    @Test
    internal fun `Should create Rename event`() {
        // given
        val type = "league-renamed"
        val name = "Transformers"
        // when
        val event = Renamed(type, aggregateId, version, timestamp, name)
        // then
        assertEquals(type, event.type)
        assertEquals(aggregateId, event.aggregateId)
        assertEquals(version, event.version)
        assertEquals(timestamp, event.timestamp)
        assertEquals(name, event.name)
    }

    @Test
    internal fun `Should create SettingsChanged event`() {
        // given
        val type = "league-settings-changed"
        val allowDraws = true
        val maxScore = 10
        // when
        val event = SettingsChanged(type, aggregateId, version, timestamp, allowDraws, maxScore)
        // then
        assertEquals(type, event.type)
        assertEquals(aggregateId, event.aggregateId)
        assertEquals(version, event.version)
        assertEquals(timestamp, event.timestamp)
        assertEquals(allowDraws, event.allowDraws)
        assertEquals(maxScore, event.maxScore)
    }
}