package com.tm.rankme.infrastructure.player

import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerPlayedGame
import com.tm.rankme.infrastructure.InfrastructureException
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

internal class PlayerMapperTest {
    private val mapper = PlayerMapper()

    @Test
    internal fun `Should serialize 'player-created' event`() {
        // given
        val event = PlayerCreated(UUID.randomUUID(), "Optimus Prime")
        // when
        val serializedEvent = mapper.serialize(event)
        // then
        (serializedEvent as Created).let {
            assertEquals(event.type, it.type)
            assertEquals(event.aggregateId, it.aggregateId)
            assertEquals(event.timestamp, it.timestamp)
            assertEquals(event.version, it.version)
            assertEquals(event.name, it.name)
            assertEquals(event.deviation, it.deviation)
            assertEquals(event.rating, it.rating)
        }
    }

    @Test
    internal fun `Should serialize 'player-played-game' event`() {
        // given
        val event = PlayerPlayedGame(-34, 52, 3, UUID.randomUUID(), 1)
        // when
        val serializedEvent = mapper.serialize(event)
        // then
        (serializedEvent as PlayedGame).let {
            assertEquals(event.type, it.type)
            assertEquals(event.aggregateId, it.aggregateId)
            assertEquals(event.timestamp, it.timestamp)
            assertEquals(event.version, it.version)
            assertEquals(event.deviationDelta, it.deviationDelta)
            assertEquals(event.ratingDelta, it.ratingDelta)
            assertEquals(event.score, it.score)
        }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val event = object : Event<Player>(UUID.randomUUID(), 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: Player) { }
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
        val leagueId = UUID.randomUUID()
        val event = """{"type": "player-created", "aggregateId": "$aggregateId", "version": 0, "timestamp": 0, 
            "leagueId": "$leagueId", "name": "Optimus Prime", "deviation": 149, "rating": 2859}""".toByteArray()        // when
        val deserializedEvent = mapper.deserialize("player-created", event)
        // then
        (deserializedEvent as PlayerCreated).let {
            assertEquals("player-created", it.type)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals(0, it.version)
            assertEquals(leagueId, it.leagueId)
            assertEquals("Optimus Prime", it.name)
            assertEquals(149, it.deviation)
            assertEquals(2859, it.rating)
        }
    }

    @Test
    internal fun `Should deserialize 'played-game' event`() {
        // given
        val aggregateId = UUID.randomUUID()
        val event = """{"type": "player-played-game", "aggregateId": "$aggregateId", "version": 1, "timestamp": 0, 
            "deviationDelta": -36, "ratingDelta": -132, "score": 2}""".toByteArray()
        // when
        val deserializedEvent = mapper.deserialize("player-played-game", event)
        // then
        (deserializedEvent as PlayerPlayedGame).let {
            assertEquals("player-played-game", it.type)
            assertEquals(aggregateId, it.aggregateId)
            assertEquals(1, it.version)
            assertEquals(-36, it.deviationDelta)
            assertEquals(-132, it.ratingDelta)
            assertEquals(2, it.score)
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val events = listOf("player-created", "player-played-game")
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