package com.tm.rankme.storage.write.player

import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamRevision
import com.eventstore.dbclient.Streams
import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
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

    private val streams = mockk<Streams>()
    private val readResult = mockk<ReadResult>()
    private val resolvedEvent = mockk<ResolvedEvent>()
    private val recordedEvent = mockk<RecordedEvent>()

    @BeforeEach
    internal fun setUp() {
        every { connector.stream } returns streams
    }

    @Test
    internal fun `Should save 'create' event with initial version 0`() {
        // given
        val event = PlayerCreated(UUID.randomUUID(), "Optimus Prime")
        every { streams.appendStream(event.aggregateId.toString()).addEvent(any()).execute().get() } returns mockk()
        // when
        eventStorage.save(event)
        // then
        verify(exactly = 0) { streams.readStream(any()) }
        verify(exactly = 1) { streams.appendStream(event.aggregateId.toString()).addEvent(any()).execute().get() }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val event = object : Event<Player>(UUID.randomUUID(), 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: Player) { }
        }
        every { streams.readStream(event.aggregateId.toString()).fromEnd().backward().execute(1).get() } returns readResult
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
        every { streams.readStream(aggregateId.toString()).fromStart().readThrough().get().events } returns
            listOf(resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returns "player-created"
        every { recordedEvent.eventData } returns
            """{"type": "player-created", "aggregateId": "$aggregateId", "version": 0, "timestamp": 0, 
            "leagueId": "$leagueId", "name": "Optimus Prime", "deviation": 149, "rating": 2859}""".toByteArray()
        // when
        val events = eventStorage.events(aggregateId.toString())
        // then
        assertEquals(1, events.size)
        events.forEach { assertEquals(aggregateId, it.aggregateId) }
        assertEquals("player-created", events[0].type)
        assertEquals(0, events[0].version)
        (events[0] as PlayerCreated).let {
            assertEquals(leagueId, it.leagueId)
            assertEquals("Optimus Prime", it.name)
            assertEquals(149, it.deviation)
            assertEquals(2859, it.rating)
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { streams.readStream(aggregateId.toString()).fromStart().readThrough().get().events } returns listOf(resolvedEvent)
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
        every { streams.readStream(aggregateId.toString()).fromStart().readThrough().get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent, resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returns "unknown-event"
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.events(aggregateId.toString()) }
        // then
        assertEquals("Cannot deserialize event 'unknown-event'", exception.message)
    }
}