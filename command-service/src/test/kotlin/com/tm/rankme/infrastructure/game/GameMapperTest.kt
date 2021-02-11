package com.tm.rankme.infrastructure.game

import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.infrastructure.InfrastructureException
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

internal class GameMapperTest {
    private val mapper = GameMapper()

    @Test
    internal fun `Should serialize 'game-scheduled' event`() {
        // given
        val event = GameScheduled(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            Instant.now().toEpochMilli(), UUID.randomUUID())
        // when
        val serializedEvent = mapper.serialize(event)
        // then
        (serializedEvent as Scheduled).let {
            assertEquals(event.type, it.type)
            assertEquals(event.aggregateId, it.aggregateId)
            assertEquals(event.timestamp, it.timestamp)
            assertEquals(event.version, it.version)
            assertEquals(event.leagueId, it.leagueId)
            assertEquals(event.dateTime, it.dateTime)
            assertEquals(event.firstId, it.firstId)
            assertEquals(event.secondId, it.secondId)
        }
    }

    @Test
    internal fun `Should serialize 'game-played' event`() {
        // given
        val event = GamePlayed(
            UUID.randomUUID(),
            UUID.randomUUID(), 5, -57, 125,
            UUID.randomUUID(), 2, -48, -132)
        // when
        val serializedEvent = mapper.serialize(event)
        // then
        (serializedEvent as Played).let {
            assertEquals(event.type, it.type)
            assertEquals(event.aggregateId, it.aggregateId)
            assertEquals(event.timestamp, it.timestamp)
            assertEquals(event.version, it.version)
            assertEquals(event.leagueId, it.leagueId)
            assertEquals(event.dateTime, it.dateTime)
            assertEquals(event.firstId, it.firstId)
            assertEquals(event.firstScore, it.firstScore)
            assertEquals(event.firstDeviationDelta, it.firstDeviationDelta)
            assertEquals(event.firstRatingDelta, it.firstRatingDelta)
            assertEquals(event.secondId, it.secondId)
            assertEquals(event.secondScore, it.secondScore)
            assertEquals(event.secondDeviationDelta, it.secondDeviationDelta)
            assertEquals(event.secondRatingDelta, it.secondRatingDelta)
        }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val game = Game.from(listOf(
            GameScheduled(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Instant.now().toEpochMilli())
        ))
        val event = object : Event<Game>(game.id, 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: Game) { }
        }
        // when
        val exception = assertFailsWith<InfrastructureException> { mapper.serialize(event) }
        // then
        assertEquals("Cannot serialize event '${event.type}'", exception.message)
    }

    @Test
    internal fun `Should deserialize 'scheduled' event`() {
        // given
        val aggregateId = UUID.randomUUID()
        val leagueId = UUID.randomUUID()
        val event = """{"type": "game-scheduled", "aggregateId": "$aggregateId", "version": 0, "timestamp": 0, "leagueId": "$leagueId", 
            "firstId": "1e56a755-1134-4f17-94fe-e6f2abe8ec07", "secondId": "bb47a873-78ed-4320-a3b9-c214e63c9f6e", 
            "dateTime": 1622276383}"""
        // when
        val deserializedEvent = mapper.deserialize("game-scheduled", event)
        // then
        (deserializedEvent as GameScheduled).let {
            assertEquals("game-scheduled", it.type)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals(0, it.version)
            assertEquals(leagueId, it.leagueId)
            assertEquals(1622276383, it.dateTime)
            assertEquals(UUID.fromString("1e56a755-1134-4f17-94fe-e6f2abe8ec07"), it.firstId)
            assertEquals(UUID.fromString("bb47a873-78ed-4320-a3b9-c214e63c9f6e"), it.secondId)
        }
    }

    @Test
    internal fun `Should deserialize 'played' event`() {
        // given
        val aggregateId = UUID.randomUUID()
        val leagueId = UUID.randomUUID()
        val event = """{"type": "game-played", "aggregateId": "$aggregateId", "version": 1, "timestamp": 0, "leagueId": "$leagueId", 
            "firstId": "1e56a755-1134-4f17-94fe-e6f2abe8ec07", "firstScore": 4,
            "firstDeviationDelta": -23, "firstRatingDelta": 78,
            "secondId": "bb47a873-78ed-4320-a3b9-c214e63c9f6e", "secondScore": 3, 
            "secondDeviationDelta": -35, "secondRatingDelta": -63, "dateTime": 1611176383}"""
        // when
        val deserializedEvent = mapper.deserialize("game-played", event)
        // then
        (deserializedEvent as GamePlayed).let {
            assertEquals("game-played", it.type)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals(1, it.version)
            assertEquals(leagueId, it.leagueId)
            assertEquals(1611176383, it.dateTime)
            assertEquals(UUID.fromString("1e56a755-1134-4f17-94fe-e6f2abe8ec07"), it.firstId)
            assertEquals(4, it.firstScore)
            assertEquals(-23, it.firstDeviationDelta)
            assertEquals(78, it.firstRatingDelta)
            assertEquals(UUID.fromString("bb47a873-78ed-4320-a3b9-c214e63c9f6e"), it.secondId)
            assertEquals(3, it.secondScore)
            assertEquals(-35, it.secondDeviationDelta)
            assertEquals(-63, it.secondRatingDelta)
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val events = listOf("game-scheduled", "game-played")
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
        val data = ByteArray(0)
        // when
        val exception = assertFailsWith<InfrastructureException> { mapper.deserialize(type, data) }
        // then
        assertEquals("Cannot deserialize event '$type'", exception.message)
    }
}