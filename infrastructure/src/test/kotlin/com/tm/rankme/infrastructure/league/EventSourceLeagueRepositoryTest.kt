package com.tm.rankme.infrastructure.league

import com.eventstore.dbclient.AppendToStream
import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.ReadStream
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamRevision
import com.eventstore.dbclient.Streams
import com.eventstore.dbclient.WriteResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.infrastructure.EventStoreConnector
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventSourceLeagueRepositoryTest {
    private val connector: EventStoreConnector = mock()
    private val repository = EventSourceLeagueRepository(connector)

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
    internal fun `Should store 'create' event with initial version 0`() {
        // given
        val league = League.create("Start Wars")
        given(streams.appendStream(league.id.toString())).willReturn(appendStream)
        given(appendStream.addEvent(any())).willReturn(appendStream)
        given(appendStream.execute()).willReturn(writeCompletableFuture)
        given(writeCompletableFuture.get()).willReturn(mock())
        // when
        repository.store(league)
        // then
        verify(streams, never()).readStream(any())
        verify(streams).appendStream(league.id.toString())
        verify(appendStream).addEvent(any())
        verify(appendStream).execute()
        verify(writeCompletableFuture).get()
    }

    @Test
    internal fun `Should store 'rename' event with version 1`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        league.rename("Transformers")
        givenCheckVersion(league.id.toString(), 0)
        given(streams.appendStream(league.id.toString())).willReturn(appendStream)
        given(appendStream.addEvent(any())).willReturn(appendStream)
        given(appendStream.execute()).willReturn(writeCompletableFuture)
        given(writeCompletableFuture.get()).willReturn(mock())
        // when
        repository.store(league)
        // then
        verify(streams).readStream(league.id.toString())
        verify(readStream).fromEnd()
        verify(readStream).backward()
        verify(readStream).execute(1)
        verify(readCompletableFuture).get()
        verify(streams).appendStream(league.id.toString())
        verify(appendStream).addEvent(any())
        verify(appendStream).execute()
        verify(writeCompletableFuture).get()
    }

    @Test
    internal fun `Should store 'change settings' event with version 2`() {
        // given
        val leagueCreated = LeagueCreated("Star Wars")
        val leagueRenamed = LeagueRenamed(leagueCreated.aggregateId, 1, "Transformers")
        val league = League.from(listOf(leagueCreated, leagueRenamed))
        league.settings(true, 4)
        givenCheckVersion(league.id.toString(), 1)
        given(streams.appendStream(league.id.toString())).willReturn(appendStream)
        given(appendStream.addEvent(any())).willReturn(appendStream)
        given(appendStream.execute()).willReturn(writeCompletableFuture)
        given(writeCompletableFuture.get()).willReturn(mock())
        // when
        repository.store(league)
        // then
        verify(streams).readStream(league.id.toString())
        verify(readStream).fromEnd()
        verify(readStream).backward()
        verify(readStream).execute(1)
        verify(readCompletableFuture).get()
        verify(streams).appendStream(league.id.toString())
        verify(appendStream).addEvent(any())
        verify(appendStream).execute()
        verify(writeCompletableFuture).get()
    }

    @Test
    internal fun `Should throw exception when cannot get actual aggregate version`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        league.rename("Transformers")
        givenCheckVersion(league.id.toString(), 0)
        given(readResult.events).willReturn(emptyList())
        // when
        val exception = assertFailsWith<AggregateException> { repository.store(league) }
        // then
        assertEquals("Cannon get actual version of league id=${league.id}", exception.message)
    }

    @Test
    internal fun `Should throw exception when event version is out of date`() {
        // given
        val league = League.from(listOf(LeagueCreated("Star Wars")))
        league.rename("Transformers")
        givenCheckVersion(league.id.toString(), 15)
        // when
        val exception = assertFailsWith<AggregateException> { repository.store(league) }
        // then
        assertEquals("Version mismatch of league id=${league.id}", exception.message)
    }

    @Test
    internal fun `Should build league aggregate from events stream`() {
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
        val league = repository.byId(aggregateId)
        // then
        assertEquals(aggregateId, league.id)
        assertEquals(2, league.version)
        assertEquals("Transformers", league.name)
        assertEquals(true, league.settings.allowDraws)
        assertEquals(10, league.settings.maxScore)
    }

    @Test
    internal fun `Should throw exception when event from stream is not known`() {
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
        val exception = assertFailsWith<AggregateException> { repository.byId(aggregateId) }
        // then
        assertEquals("Event 'unknown-event' is not known", exception.message)
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