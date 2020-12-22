package com.tm.rankme.storage.write.league

import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamRevision
import com.eventstore.dbclient.Streams
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
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

internal class LeagueEventStorageTest {
    private val connector = mockk<EventStoreConnector>()
    private val eventStorage = LeagueEventStorage(connector)

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
        val event = LeagueCreated("Star Wars", aggregateId = UUID.randomUUID())
        every { streams.appendStream(event.aggregateId.toString()).addEvent(any()).execute().get() } returns mockk()
        // when
        eventStorage.save(event)
        // then
        verify(exactly = 0) { streams.readStream(any()) }
        verify(exactly = 1) { streams.appendStream(event.aggregateId.toString()).addEvent(any()).execute().get() }
    }

    @Test
    internal fun `Should save 'rename' event with version 1`() {
        // given
        val event = LeagueRenamed(UUID.randomUUID(), 1, "Transformers")
        every { streams.readStream(event.aggregateId.toString()).fromEnd().backward().execute(1).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        every { streams.appendStream(event.aggregateId.toString()).addEvent(any()).execute().get() } returns mockk()
        // when
        eventStorage.save(event)
        // then
        verify(exactly = 1) { streams.readStream(event.aggregateId.toString()).fromEnd().backward().execute(1).get() }
        verify(exactly = 1) { streams.appendStream(event.aggregateId.toString()).addEvent(any()).execute().get() }
    }

    @Test
    internal fun `Should save 'change settings' event with version 2`() {
        // given
        val event = LeagueSettingsChanged(UUID.randomUUID(), 2, true, 4)
        every { streams.readStream(event.aggregateId.toString()).fromEnd().backward().execute(1).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(1)
        every { streams.appendStream(event.aggregateId.toString()).addEvent(any()).execute().get() } returns mockk()
        // when
        eventStorage.save(event)
        // then
        verify(exactly = 1) { streams.readStream(event.aggregateId.toString()).fromEnd().backward().execute(1).get() }
        verify(exactly = 1) { streams.appendStream(event.aggregateId.toString()).addEvent(any()).execute().get() }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val event = object : Event<League>(UUID.randomUUID(), 1, 0) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: League) { }
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
        every { streams.readStream(aggregateId.toString()).fromStart().readThrough().get().events } returns
            listOf(resolvedEvent, resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returnsMany listOf("league-created", "league-renamed", "league-settings-changed")
        every { recordedEvent.eventData } returnsMany listOf(
            """{"type": "league-created", "aggregateId": "$aggregateId", 
                "version": 0, "timestamp": 0, "name": "Star Wars", "allowDraws": false, "maxScore": 2}""".toByteArray(),
            """{"type": "league-renamed", "aggregateId": "$aggregateId", 
                "version": 1, "timestamp": 0, "name": "Transformers"}""".toByteArray(),
            """{"type": "league-settings-changed", "aggregateId": "$aggregateId", 
                "version": 2, "timestamp": 0, "allowDraws": true, "maxScore": 10}""".toByteArray()
        )
        every { recordedEvent.getEventDataAs(LeagueRenamed::class.java) } returns
            LeagueRenamed(aggregateId, 1, "Transformers")
        every { recordedEvent.getEventDataAs(LeagueSettingsChanged::class.java) } returns
            LeagueSettingsChanged(aggregateId, 2, true, 5)
        // when
        val events = eventStorage.events(aggregateId.toString())
        // then
        assertEquals(3, events.size)
        events.forEach { assertEquals(aggregateId, it.aggregateId) }
        assertEquals("league-created", events[0].type)
        assertEquals(0, events[0].version)
        assertEquals("Star Wars", (events[0] as LeagueCreated).name)

        assertEquals("league-renamed", events[1].type)
        assertEquals(1, events[1].version)
        assertEquals("Transformers", (events[1] as LeagueRenamed).name)

        assertEquals("league-settings-changed", events[2].type)
        assertEquals(2, events[2].version)
        assertEquals(true, (events[2] as LeagueSettingsChanged).allowDraws)
        assertEquals(10, (events[2] as LeagueSettingsChanged).maxScore)
    }

    @Test
    internal fun `Should throw exception when cannot deserialize event`() {
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