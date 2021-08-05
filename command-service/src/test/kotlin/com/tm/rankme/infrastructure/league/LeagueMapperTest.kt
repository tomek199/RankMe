package com.tm.rankme.infrastructure.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.infrastructure.InfrastructureException
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LeagueMapperTest {
    private val mapper = LeagueMapper()

    @Test
    internal fun `Should serialize 'league-created' event`() {
        // given
        val event = LeagueCreated("Star Wars", true, 3, randomNanoId())
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
        val event = LeagueRenamed(randomNanoId(), 1, "Star Wars")
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
        val event = LeagueSettingsChanged(randomNanoId(), 2, true, 5)
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
        val event = object : Event<League>(randomNanoId(), 1) {
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
        val aggregateId = randomNanoId()
        val event = """{"type": "league-created", "aggregateId": "$aggregateId", 
                "version": 0, "timestamp": 12345, "name": "Star Wars", "allowDraws": false, "maxScore": 2}"""
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
        val aggregateId = randomNanoId()
        val event = """{"type": "league-renamed", "aggregateId": "$aggregateId", 
                "version": 1, "timestamp": 12345, "name": "Transformers"}"""
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
        val aggregateId = randomNanoId()
        val event = """{"type": "league-settings-changed", "aggregateId": "$aggregateId", 
                "version": 2, "timestamp": 12345, "allowDraws": true, "maxScore": 5}"""
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
            val data = "$it-invalid-json"
            // then
            assertFailsWith<JsonParseException> { mapper.deserialize(it, data) }
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize unknown event type`() {
        // given
        val type = "unknown-event"
        val data = "unknown-event-data"
        // when
        val exception = assertFailsWith<InfrastructureException> { mapper.deserialize(type, data) }
        // then
        assertEquals("Cannot deserialize event '$type'", exception.message)
    }
}