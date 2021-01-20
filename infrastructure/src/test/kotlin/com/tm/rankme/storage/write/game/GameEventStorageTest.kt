package com.tm.rankme.storage.write.game

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamRevision
import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.storage.write.EventStoreConnector
import com.tm.rankme.storage.write.InfrastructureException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameEventStorageTest {
    private val connector = mockk<EventStoreConnector>()
    private val eventStorage = GameEventStorage(connector)

    private val client = mockk<EventStoreDBClient>()
    private val readResult = mockk<ReadResult>()
    private val resolvedEvent = mockk<ResolvedEvent>()
    private val recordedEvent = mockk<RecordedEvent>()

    @BeforeEach
    internal fun setUp() {
        every { connector.client } returns client
    }

    @Test
    internal fun `Should save 'game-played' event with initial version 0`() {
        // given
        val event = GamePlayed(UUID.randomUUID(), UUID.randomUUID(), 3, -42, 145,
            UUID.randomUUID(), 2, -52, -143, aggregateId = UUID.randomUUID())
        every { client.appendToStream(event.aggregateId.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        eventStorage.save(event)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { client.appendToStream(event.aggregateId.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should save 'game-scheduled' event with initial version 0`() {
        // given
        val event = GameScheduled(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
        every { client.appendToStream(event.aggregateId.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        eventStorage.save(event)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { client.appendToStream(event.aggregateId.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val event = object : Event<Game>(UUID.randomUUID(), 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: Game) { }
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
        every { recordedEvent.eventType } returnsMany listOf("game-scheduled", "game-played")
        every { recordedEvent.eventData } returnsMany listOf(
            """{"type": "game-scheduled", "aggregateId": "$aggregateId", "version": 0, "timestamp": 0, "leagueId": "$leagueId", 
            "firstId": "1e56a755-1134-4f17-94fe-e6f2abe8ec07", "secondId": "bb47a873-78ed-4320-a3b9-c214e63c9f6e", 
            "dateTime": 1622276383}""".toByteArray(),
            """{"type": "game-played", "aggregateId": "$aggregateId", "version": 1, "timestamp": 0, "leagueId": "$leagueId", 
            "firstId": "1e56a755-1134-4f17-94fe-e6f2abe8ec07", "firstScore": 4,
            "firstDeviationDelta": -23, "firstRatingDelta": 78,
            "secondId": "bb47a873-78ed-4320-a3b9-c214e63c9f6e", "secondScore": 3, 
            "secondDeviationDelta": -35, "secondRatingDelta": -63, "dateTime": 1611176383}""".toByteArray()
        )
        // when
        val events = eventStorage.events(aggregateId.toString())
        // then
        assertEquals(2, events.size)
        events.forEach { assertEquals(aggregateId, it.aggregateId) }
        (events[0] as GameScheduled).let {
            assertEquals("game-scheduled", it.type)
            assertEquals(0, it.version)
            assertEquals(leagueId, it.leagueId)
            assertEquals(UUID.fromString("1e56a755-1134-4f17-94fe-e6f2abe8ec07"), it.firstId)
            assertEquals(UUID.fromString("bb47a873-78ed-4320-a3b9-c214e63c9f6e"), it.secondId)
            assertEquals(1622276383, it.dateTime)
        }
        (events[1] as GamePlayed).let {
            assertEquals("game-played", it.type)
            assertEquals(0, it.version)
            assertEquals(leagueId, it.leagueId)
            assertEquals(UUID.fromString("1e56a755-1134-4f17-94fe-e6f2abe8ec07"), it.firstId)
            assertEquals(4, it.firstScore)
            assertEquals(-23, it.firstDeviationDelta)
            assertEquals(78, it.firstRatingDelta)
            assertEquals(UUID.fromString("bb47a873-78ed-4320-a3b9-c214e63c9f6e"), it.secondId)
            assertEquals(3, it.secondScore)
            assertEquals(-35, it.secondDeviationDelta)
            assertEquals(-63, it.secondRatingDelta)
            assertEquals(1611176383, it.dateTime)
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get().events } returns listOf(resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returns "game-played"
        every { recordedEvent.eventData } returns "game-played-invalid-json".toByteArray()
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