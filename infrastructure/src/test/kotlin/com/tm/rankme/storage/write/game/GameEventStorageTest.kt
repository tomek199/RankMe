package com.tm.rankme.storage.write.game

import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamRevision
import com.eventstore.dbclient.Streams
import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
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

internal class GameEventStorageTest {
    private val connector = mockk<EventStoreConnector>()
    private val eventStorage = GameEventStorage(connector)

    private val streams = mockk<Streams>()
    private val readResult = mockk<ReadResult>()
    private val resolvedEvent = mockk<ResolvedEvent>()
    private val recordedEvent = mockk<RecordedEvent>()

    @BeforeEach
    internal fun setUp() {
        every { connector.stream } returns streams
    }

    @Test
    internal fun `Should save 'game-played' event with initial version 0`() {
        // given
        val event = GamePlayed(UUID.randomUUID(), UUID.randomUUID(), 3, -42, 145,
            UUID.randomUUID(), 2, -52, -143, UUID.randomUUID())
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
        val event = object : Event<Game>(UUID.randomUUID(), 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: Game) { }
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
        every { recordedEvent.eventType } returnsMany listOf("game-played")
        every { recordedEvent.eventData } returnsMany listOf(
            """{"type": "game-played", "aggregateId": "$aggregateId", "version": 0, "timestamp": 0, "leagueId": "$leagueId", 
            "firstId": "1e56a755-1134-4f17-94fe-e6f2abe8ec07", "firstScore": 4,
            "firstDeviationDelta": -23, "firstRatingDelta": 78,
            "secondId": "bb47a873-78ed-4320-a3b9-c214e63c9f6e", "secondScore": 3, 
            "secondDeviationDelta": -35, "secondRatingDelta": -63}""".toByteArray()
        )
        // when
        val events = eventStorage.events(aggregateId.toString())
        // then
        assertEquals(1, events.size)
        events.forEach { assertEquals(aggregateId, it.aggregateId) }
        (events[0] as GamePlayed).let {
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
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { streams.readStream(aggregateId.toString()).fromStart().readThrough().get().events } returns listOf(resolvedEvent)
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