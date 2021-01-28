package com.tm.rankme.storage.write

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamRevision
import com.fasterxml.jackson.core.JsonParseException
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventStoreLeagueRepositoryTest {
    private val connector = mockk<EventStoreConnector>()
    private val repository = EventStoreLeagueRepository(connector)

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
        val league = League.create("Star Wars")
        every { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(league)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should save 'rename' event with version 1`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        league.rename("Transformers")
        every { client.readStream(league.id.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        every { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(league)
        // then
        verify(exactly = 1) { client.readStream(league.id.toString(), 1, ofType(ReadStreamOptions::class)).get() }
        verify(exactly = 1) { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should save 'change settings' event with version 1`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        league.settings(true, 4)
        every { client.readStream(league.id.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        every { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(league)
        // then
        verify(exactly = 1) { client.readStream(league.id.toString(), 1, ofType(ReadStreamOptions::class)).get() }
        verify(exactly = 1) { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        val event = object : Event<League>(league.id, 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: League) { }
        }
        league.pendingEvents.add(event)
        every { client.readStream(league.id.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.store(league) }
        // then
        assertEquals("Cannot serialize event '${event.type}'", exception.message)
    }

    @Test
    internal fun `Should return aggregate from events`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get().events } returns
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
        // when
        val league = repository.byId(aggregateId)
        // then
        assertEquals(aggregateId, league.id)
        assertEquals(2, league.version)
        assertTrue(league.pendingEvents.isEmpty())
        assertEquals("Transformers", league.name)
        assertTrue(league.settings.allowDraws)
        assertEquals(10, league.settings.maxScore)
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val events = listOf("league-created", "league-renamed", "league-settings-changed")
        // when
        events.forEach {
            val aggregateId = UUID.randomUUID()
            every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get().events } returns listOf(resolvedEvent)
            every { resolvedEvent.originalEvent } returns recordedEvent
            every { recordedEvent.eventType } returns it
            every { recordedEvent.eventData } returns "$it-invalid-json".toByteArray()
            // then
            assertFailsWith<JsonParseException> { repository.byId(aggregateId) }
        }
    }

    @Test
    internal fun `Should throw exception when cannot deserialize null event type`() {
        val aggregateId = UUID.randomUUID()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent, resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returns null
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.byId(aggregateId) }
        // then
        assertEquals("Cannot deserialize event 'null'", exception.message)
    }

    @Test
    internal fun `Should throw exception when cannot deserialize unknown event type`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent, resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returns "unknown-event"
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.byId(aggregateId) }
        // then
        assertEquals("Cannot deserialize event 'unknown-event'", exception.message)
    }

    @Test
    internal fun `Should return true when league exist`() {
        // given
        val leagueId = UUID.randomUUID()
        every { client.readStream(leagueId.toString(), ofType(ReadStreamOptions::class)).get().events } returns
            listOf(resolvedEvent, resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returnsMany listOf("league-created")
        every { recordedEvent.eventData } returnsMany listOf(
            """{"type": "league-created", "aggregateId": "$leagueId", 
                "version": 0, "timestamp": 0, "name": "Star Wars", "allowDraws": false, "maxScore": 2}""".toByteArray()
        )
        // then
        assertTrue(repository.exist(leagueId))
    }

    @Test
    internal fun `Should return false when league does not exist`() {
        // given
        val leagueId = UUID.randomUUID()
        every { client.readStream(leagueId.toString(), ofType(ReadStreamOptions::class)).get().events } throws
            InfrastructureException("Stream is not found")
        // then
        assertFalse(repository.exist(leagueId))
    }

    @Test
    internal fun `Should return false when league does not contain any event`() {
        // given
        val leagueId = UUID.randomUUID()
        every { client.readStream(leagueId.toString(), ofType(ReadStreamOptions::class)).get().events } returns emptyList()
        // then
        assertFalse(repository.exist(leagueId))
    }
}