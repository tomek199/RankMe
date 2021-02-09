package com.tm.rankme.infrastructure.league

import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.infrastructure.InfrastructureException
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

internal class LeagueMapperTest {
    private val mapper = LeagueMapper()

    @Test
    internal fun `Should serialize 'league-created' event`() {
        // given
        val event = LeagueCreated("Star Wars", true, 3, UUID.randomUUID())
        // when
        val serializedEvent = mapper.serialize(event)
        // then
        (serializedEvent as Created).let {
            assertEquals(event.type, it.type)
            assertEquals(event.aggregateId, it.aggregateId)
            assertEquals(event.timestamp, it.timestamp)
            assertEquals(event.version, it.version)
            assertEquals(event.name, it.name)
            assertEquals(event.allowDraws, it.allowDraws)
            assertEquals(event.maxScore, it.maxScore)
        }
    }

    @Test
    internal fun `Should serialize 'league-renamed' event`() {
        // given
        val event = LeagueRenamed(UUID.randomUUID(), 1, "Star Wars")
        // when
        val serializedEvent = mapper.serialize(event)
        // then
        (serializedEvent as Renamed).let {
            assertEquals(event.type, it.type)
            assertEquals(event.aggregateId, it.aggregateId)
            assertEquals(event.timestamp, it.timestamp)
            assertEquals(event.version, it.version)
            assertEquals(event.name, it.name)
        }
    }

    @Test
    internal fun `Should serialize 'league-settings-changed' event`() {
        // given
        val event = LeagueSettingsChanged(UUID.randomUUID(), 2, true, 5)
        // when
        val serializedEvent = mapper.serialize(event)
        // then
        (serializedEvent as SettingsChanged).let {
            assertEquals(event.type, it.type)
            assertEquals(event.aggregateId, it.aggregateId)
            assertEquals(event.timestamp, it.timestamp)
            assertEquals(event.version, it.version)
            assertEquals(event.allowDraws, it.allowDraws)
            assertEquals(event.maxScore, it.maxScore)
        }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val event = object : Event<League>(UUID.randomUUID(), 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: League) { }
        }
        // when
        val exception = assertFailsWith<InfrastructureException> { mapper.serialize(event) }
        // then
        assertEquals("Cannot serialize event '${event.type}'", exception.message)
    }

    @Test
    internal fun `Should deserialize 'created' event`() {
        // given
        val aggregateId = UUID.randomUUID()
        val event = """{"type": "league-created", "aggregateId": "$aggregateId", 
                "version": 0, "timestamp": 12345, "name": "Star Wars", "allowDraws": false, "maxScore": 2}""".toByteArray()
        // when
        val deserializedEvent = mapper.deserialize("league-created", event)
        // then
        (deserializedEvent as LeagueCreated).let {
            assertEquals("league-created", it.type)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals(0, it.version)
            assertEquals("Star Wars", it.name)
            assertEquals(false, it.allowDraws)
            assertEquals(2, it.maxScore)
        }
    }

    @Test
    internal fun `Should deserialize 'renamed' event`() {
        // given
        val aggregateId = UUID.randomUUID()
        val event = """{"type": "league-renamed", "aggregateId": "$aggregateId", 
                "version": 1, "timestamp": 12345, "name": "Transformers"}""".toByteArray()
        // when
        val deserializedEvent = mapper.deserialize("league-renamed", event)
        // then
        (deserializedEvent as LeagueRenamed).let {
            assertEquals("league-renamed", it.type)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals(1, it.version)
            assertEquals("Transformers", it.name)
        }
    }

    @Test
    internal fun `Should deserialize 'settings-changed' event`() {
        // given
        val aggregateId = UUID.randomUUID()
        val event = """{"type": "league-settings-changed", "aggregateId": "$aggregateId", 
                "version": 2, "timestamp": 12345, "allowDraws": true, "maxScore": 5}""".toByteArray()
        // when
        val deserializedEvent = mapper.deserialize("league-settings-changed", event)
        // then
        (deserializedEvent as LeagueSettingsChanged).let {
            assertEquals("league-settings-changed", it.type)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals(2, it.version)
            assertEquals(true, it.allowDraws)
            assertEquals(5, it.maxScore)
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val events = listOf("league-created", "league-renamed", "league-settings-changed")
        // when
        events.forEach {
            val data = "$it-invalid-json".toByteArray()
            // then
            assertFailsWith<JsonParseException> { mapper.deserialize(it, data) }
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize unknown event type`() {
        // given
        val type = "unknown-event"
        val data = ByteArray(0)
        // when
        val exception = assertFailsWith<InfrastructureException> { mapper.deserialize(type, data) }
        // then
        assertEquals("Cannot deserialize event '$type'", exception.message)
    }
}