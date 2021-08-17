package com.tm.rankme.infrastructure.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.infrastructure.InfrastructureException
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class GameMapperTest {
    private val mapper = GameMapper()

    @Test
    internal fun `Should serialize 'game-scheduled' event`() {
        // given
        val event = GameScheduled(randomNanoId(), randomNanoId(), randomNanoId(),
            Instant.now().toEpochMilli(), randomNanoId())
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
            randomNanoId(),
            randomNanoId(), 5, 286, -57, 1841, 125,
            randomNanoId(), 2, 312, -48, 1407, -132)
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
            assertEquals(event.firstDeviation, it.firstDeviation)
            assertEquals(event.firstDeviationDelta, it.firstDeviationDelta)
            assertEquals(event.firstRating, it.firstRating)
            assertEquals(event.firstRatingDelta, it.firstRatingDelta)
            assertEquals(event.secondId, it.secondId)
            assertEquals(event.secondScore, it.secondScore)
            assertEquals(event.secondDeviation, it.secondDeviation)
            assertEquals(event.secondDeviationDelta, it.secondDeviationDelta)
            assertEquals(event.secondRating, it.secondRating)
            assertEquals(event.secondRatingDelta, it.secondRatingDelta)
        }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val game = Game.from(listOf(
            GameScheduled(randomNanoId(), randomNanoId(), randomNanoId(), Instant.now().toEpochMilli())
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
        val aggregateId = randomNanoId()
        val leagueId = randomNanoId()
        val event = """{"type": "game-scheduled", "aggregateId": "$aggregateId", "version": 0, "timestamp": 0, "leagueId": "$leagueId", 
            "firstId": "gl7qIh4I1OYbRUA9We9IM", "secondId": "eOrItOXv2vVRWwe2awH-8", 
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
            assertEquals("gl7qIh4I1OYbRUA9We9IM", it.firstId)
            assertEquals("eOrItOXv2vVRWwe2awH-8", it.secondId)
        }
    }

    @Test
    internal fun `Should deserialize 'played' event`() {
        // given
        val aggregateId = randomNanoId()
        val leagueId = randomNanoId()
        val event = """{"type": "game-played", "aggregateId": "$aggregateId", "version": 1, "timestamp": 0, "leagueId": "$leagueId", 
            "firstId": "gl7qIh4I1OYbRUA9We9IM", "firstScore": 4,
            "firstDeviation": 184, "firstDeviationDelta": -23, "firstRating": 2864, "firstRatingDelta": 78,
            "secondId": "eOrItOXv2vVRWwe2awH-8", "secondScore": 3, 
            "secondDeviation": 285, "secondDeviationDelta": -35, "secondRating": 1981, "secondRatingDelta": -63, 
            "dateTime": 1611176383}"""
        // when
        val deserializedEvent = mapper.deserialize("game-played", event)
        // then
        (deserializedEvent as GamePlayed).let {
            assertEquals("game-played", it.type)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals(1, it.version)
            assertEquals(leagueId, it.leagueId)
            assertEquals(1611176383, it.dateTime)
            assertEquals("gl7qIh4I1OYbRUA9We9IM", it.firstId)
            assertEquals(4, it.firstScore)
            assertEquals(184, it.firstDeviation)
            assertEquals(-23, it.firstDeviationDelta)
            assertEquals(2864, it.firstRating)
            assertEquals(78, it.firstRatingDelta)
            assertEquals("eOrItOXv2vVRWwe2awH-8", it.secondId)
            assertEquals(3, it.secondScore)
            assertEquals(285, it.secondDeviation)
            assertEquals(-35, it.secondDeviationDelta)
            assertEquals(1981, it.secondRating)
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