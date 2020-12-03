package com.tm.rankme.infrastructure.league

import com.eventstore.dbclient.AppendToStream
import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.ReadStream
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamNotFoundException
import com.eventstore.dbclient.StreamRevision
import com.eventstore.dbclient.Streams
import com.eventstore.dbclient.WriteResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.infrastructure.EventStoreConnector
import com.tm.rankme.infrastructure.InfrastructureException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LeagueEventStorageTest {
    private val connector: EventStoreConnector = mock()
    private val eventStorage = LeagueEventStorage(connector)

    private val streams: Streams = mock()
    private val appendStream: AppendToStream = mock()
    private val writeCompletableFuture: CompletableFuture<WriteResult> = mock()
    private val readStream: ReadStream = mock()
    private val readCompletableFuture: CompletableFuture<ReadResult> = mock()
    private val readResult: ReadResult = mock()
    private val resolvedEvent: ResolvedEvent = mock()
    private val recordedEvent: RecordedEvent = mock()

    @BeforeEach
    internal fun setUp() {
        given(connector.stream).willReturn(streams)
    }

    @Test
    internal fun `Should save 'create' event with initial version 0`() {
        // given
        val event = LeagueCreated("Star Wars", aggregateId = UUID.randomUUID())
        given(streams.appendStream(event.aggregateId.toString())).willReturn(appendStream)
        given(appendStream.addEvent(any())).willReturn(appendStream)
        given(appendStream.execute()).willReturn(writeCompletableFuture)
        given(writeCompletableFuture.get()).willReturn(mock())
        // when
        eventStorage.save(event)
        // then
        verify(streams, never()).readStream(any())
        verify(streams).appendStream(event.aggregateId.toString())
        verify(appendStream).addEvent(any())
        verify(appendStream).execute()
        verify(writeCompletableFuture).get()
    }

    @Test
    internal fun `Should save 'rename' event with version 1`() {
        // given
        val event = LeagueRenamed(UUID.randomUUID(), 1, "Transformers")
        givenCheckVersion(event.aggregateId.toString(), 0)
        given(streams.appendStream(event.aggregateId.toString())).willReturn(appendStream)
        given(appendStream.addEvent(any())).willReturn(appendStream)
        given(appendStream.execute()).willReturn(writeCompletableFuture)
        given(writeCompletableFuture.get()).willReturn(mock())
        // when
        eventStorage.save(event)
        // then
        verify(streams).readStream(event.aggregateId.toString())
        verify(readStream).fromEnd()
        verify(readStream).backward()
        verify(readStream).execute(1)
        verify(readCompletableFuture).get()
        verify(streams).appendStream(event.aggregateId.toString())
        verify(appendStream).addEvent(any())
        verify(appendStream).execute()
        verify(writeCompletableFuture).get()
    }

    @Test
    internal fun `Should save 'change settings' event with version 2`() {
        // given
        val event = LeagueSettingsChanged(UUID.randomUUID(), 2, true, 4)
        givenCheckVersion(event.aggregateId.toString(), 1)
        given(streams.appendStream(event.aggregateId.toString())).willReturn(appendStream)
        given(appendStream.addEvent(any())).willReturn(appendStream)
        given(appendStream.execute()).willReturn(writeCompletableFuture)
        given(writeCompletableFuture.get()).willReturn(mock())
        // when
        eventStorage.save(event)
        // then
        verify(streams).readStream(event.aggregateId.toString())
        verify(readStream).fromEnd()
        verify(readStream).backward()
        verify(readStream).execute(1)
        verify(readCompletableFuture).get()
        verify(streams).appendStream(event.aggregateId.toString())
        verify(appendStream).addEvent(any())
        verify(appendStream).execute()
        verify(writeCompletableFuture).get()
    }

    @Test
    internal fun `Should throw exception when cannot get actual aggregate version`() {
        // given
        val event = LeagueRenamed(UUID.randomUUID(), 1, "Star Wars")
        givenCheckVersion(event.aggregateId.toString(), 0)
        given(readResult.events).willReturn(emptyList())
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.save(event) }
        // then
        assertEquals("Cannon get actual version of league id=${event.aggregateId}", exception.message)
    }

    @Test
    internal fun `Should throw exception when event version is out of date`() {
        // given
        val event = LeagueRenamed(UUID.randomUUID(), 1, "Transformers")
        givenCheckVersion(event.aggregateId.toString(), 15)
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.save(event) }
        // then
        assertEquals("Version mismatch of league id=${event.aggregateId}", exception.message)
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val event = object : Event<League>(UUID.randomUUID(), 1, 0) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: League) { }
        }
        givenCheckVersion(event.aggregateId.toString(), 0)
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.save(event) }
        // then
        assertEquals("Cannot serialize event '${event.type}'", exception.message)
    }

    @Test
    internal fun `Should return events for aggregate`() {
        // given
        val aggregateId = UUID.randomUUID()
        given(streams.readStream(aggregateId.toString())).willReturn(readStream)
        given(readStream.fromStart()).willReturn(readStream)
        given(readStream.readThrough()).willReturn(readCompletableFuture)
        given(readCompletableFuture.get()).willReturn(readResult)
        given(readResult.events).willReturn(listOf(resolvedEvent, resolvedEvent, resolvedEvent))
        given(resolvedEvent.originalEvent).willReturn(recordedEvent)
        given(recordedEvent.eventType)
            .willReturn("league-created")
            .willReturn("league-renamed")
            .willReturn("league-settings-changed")
        given(recordedEvent.eventData)
            .willReturn("""{"type": "league-created", "aggregateId": "$aggregateId", 
                "version": 0, "timestamp": 0, "name": "Star Wars", "allowDraws": false, "maxScore": 2}""".toByteArray())
            .willReturn("""{"type": "league-renamed", "aggregateId": "$aggregateId", 
                "version": 1, "timestamp": 0, "name": "Transformers"}""".toByteArray())
            .willReturn("""{"type": "league-settings-changed", "aggregateId": "$aggregateId", 
                "version": 2, "timestamp": 0, "allowDraws": true, "maxScore": 10}""".toByteArray())
        given(recordedEvent.getEventDataAs(LeagueRenamed::class.java))
            .willReturn(LeagueRenamed(aggregateId, 1, "Transformers"))
        given(recordedEvent.getEventDataAs(LeagueSettingsChanged::class.java))
            .willReturn(LeagueSettingsChanged(aggregateId, 2, true, 5))
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
    internal fun `Should throw exception when stream is not found`() {
        // given
        val aggregateId = UUID.randomUUID()
        given(streams.readStream(aggregateId.toString())).willReturn(readStream)
        given(readStream.fromStart()).willReturn(readStream)
        given(readStream.readThrough()).willReturn(readCompletableFuture)
        given(readCompletableFuture.get())
            .willThrow(ExecutionException("Stream not found exception", StreamNotFoundException()))
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.events(aggregateId.toString()) }
        // then
        assertEquals("Stream $aggregateId is not found", exception.message)
    }

    @Test
    internal fun `Should throw exception when cannot deserialize event`() {
        // given
        val aggregateId = UUID.randomUUID()
        given(streams.readStream(aggregateId.toString())).willReturn(readStream)
        given(readStream.fromStart()).willReturn(readStream)
        given(readStream.readThrough()).willReturn(readCompletableFuture)
        given(readCompletableFuture.get()).willReturn(readResult)
        given(readResult.events).willReturn(listOf(resolvedEvent, resolvedEvent, resolvedEvent))
        given(resolvedEvent.originalEvent).willReturn(recordedEvent)
        given(recordedEvent.eventType)
            .willReturn("unknown-event")
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.events(aggregateId.toString()) }
        // then
        assertEquals("Cannot deserialize event 'unknown-event'", exception.message)
    }

    private fun givenCheckVersion(streamId: String, version: Long) {
        given(streams.readStream(streamId)).willReturn(readStream)
        given(readStream.fromEnd()).willReturn(readStream)
        given(readStream.backward()).willReturn(readStream)
        given(readStream.execute(1)).willReturn(readCompletableFuture)
        given(readCompletableFuture.get()).willReturn(readResult)
        given(readResult.events).willReturn(listOf(resolvedEvent))
        given(resolvedEvent.event).willReturn(recordedEvent)
        given(recordedEvent.streamRevision).willReturn(StreamRevision(version))
    }
}