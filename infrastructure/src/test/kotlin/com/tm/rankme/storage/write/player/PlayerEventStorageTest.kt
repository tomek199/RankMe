package com.tm.rankme.storage.write.player

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamRevision
import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerPlayedGame
import com.tm.rankme.storage.write.EventStoreConnector
import com.tm.rankme.storage.write.InfrastructureException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PlayerEventStorageTest {
    private val connector = mockk<EventStoreConnector>()
    private val eventStorage = PlayerEventStorage(connector)

    private val client = mockk<EventStoreDBClient>()
    private val readResult = mockk<ReadResult>()
    private val resolvedEvent = mockk<ResolvedEvent>()
    private val recordedEvent = mockk<RecordedEvent>()

    @BeforeEach
    internal fun setUp() {
        every { connector.client } returns client
    }

    @Test
    internal fun `Should save 'create' event with initial version 0`() {
        // given
        val event = PlayerCreated(UUID.randomUUID(), "Optimus Prime")
        every { client.appendToStream(event.aggregateId.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        eventStorage.save(event)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { client.appendToStream(event.aggregateId.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should save 'played-game' event with version 1`() {
        // given
        val event = PlayerPlayedGame(-34, 76, 3, UUID.randomUUID(), 1)
        every { client.readStream(event.aggregateId.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        every { client.appendToStream(event.aggregateId.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        eventStorage.save(event)
        // then
        verify(exactly = 1) { client.readStream(event.aggregateId.toString(), 1, ofType(ReadStreamOptions::class)).get() }
        verify(exactly = 1) { client.appendToStream(event.aggregateId.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val event = object : Event<Player>(UUID.randomUUID(), 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: Player) { }
        }
        every { client.readStream(event.aggregateId.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.save(event) }
        // then
        assertEquals("Cannot serialize event '${event.type}'", exception.message)
    }

    @Test
    internal fun `Should return events for aggregate`() {
        // given
        val aggregateId = UUID.randomUUID()
        val leagueId = UUID.randomUUID()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get().events } returns
            listOf(resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returnsMany listOf("player-created", "player-played-game")
        every { recordedEvent.eventData } returnsMany listOf(
            """{"type": "player-created", "aggregateId": "$aggregateId", "version": 0, "timestamp": 0, 
            "leagueId": "$leagueId", "name": "Optimus Prime", "deviation": 149, "rating": 2859}""".toByteArray(),
            """{"type": "player-played-game", "aggregateId": "$aggregateId", "version": 1, "timestamp": 0, 
            "deviationDelta": -36, "ratingDelta": -132, "score": 2}""".toByteArray(),
        )
        // when
        val events = eventStorage.events(aggregateId.toString())
        // then
        assertEquals(2, events.size)
        events.forEach { assertEquals(aggregateId, it.aggregateId) }
        (events[0] as PlayerCreated).let {
            assertEquals("player-created", it.type)
            assertEquals(0, it.version)
            assertEquals(leagueId, it.leagueId)
            assertEquals("Optimus Prime", it.name)
            assertEquals(149, it.deviation)
            assertEquals(2859, it.rating)
        }
        (events[1] as PlayerPlayedGame).let {
            assertEquals("player-played-game", it.type)
            assertEquals(1, it.version)
            assertEquals(-36, it.deviationDelta)
            assertEquals(-132, it.ratingDelta)
            assertEquals(2, it.score)
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get().events } returns listOf(resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returns "player-created"
        every { recordedEvent.eventData } returns "player-created-invalid-json".toByteArray()
        // then
        assertFailsWith<JsonParseException> { eventStorage.events(aggregateId.toString()) }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize unknown event`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent, resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returns "unknown-event"
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.events(aggregateId.toString()) }
        // then
        assertEquals("Cannot deserialize event 'unknown-event'", exception.message)
    }
}