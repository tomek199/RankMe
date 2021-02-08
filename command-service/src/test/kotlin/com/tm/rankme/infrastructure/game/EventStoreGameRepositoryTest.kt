package com.tm.rankme.infrastructure.game

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
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.domain.game.Result
import com.tm.rankme.infrastructure.EventStoreConnector
import com.tm.rankme.infrastructure.InfrastructureException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventStoreGameRepositoryTest {
    private val connector = mockk<EventStoreConnector>()
    private val repository = EventStoreGameRepository(connector)
    
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
        val game = Game.played(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            Result(0, -34, -176),
            Result(1, -42, 148)
        )
        every { client.appendToStream(game.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(game)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { client.appendToStream(game.id.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should save 'game-scheduled' event with initial version 0`() {
        // given
        val game = Game.scheduled(UUID.randomUUID(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID())
        every { client.appendToStream(game.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(game)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { client.appendToStream(game.id.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val game = Game.from(listOf(
            GameScheduled(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Instant.now().toEpochMilli())
        ))
        val event = object : Event<Game>(game.id, 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: Game) { }
        }
        game.pendingEvents.add(event)
        every { client.readStream(game.id.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.store(game) }
        // then
        assertEquals("Cannot serialize event '${event.type}'", exception.message)
    }

    @Test
    internal fun `Should return aggregate from events`() {
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
        val game = repository.byId(aggregateId)
        // then
        assertEquals(aggregateId, game.id)
        assertEquals(1, game.version)
        assertTrue(game.pendingEvents.isEmpty())
        assertEquals(leagueId, game.leagueId)
        assertEquals(UUID.fromString("1e56a755-1134-4f17-94fe-e6f2abe8ec07"), game.playerIds.first)
        assertEquals(UUID.fromString("bb47a873-78ed-4320-a3b9-c214e63c9f6e"), game.playerIds.second)
        assertEquals(Result(4, -23, 78), game.result!!.first)
        assertEquals(Result(3, -35, -63), game.result!!.second)
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val events = listOf("game-scheduled", "game-played")
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
        // given
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
}