package com.tm.rankme.infrastructure.game

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.domain.game.Result
import com.tm.rankme.infrastructure.EventStoreConnector
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventStoreGameRepositoryTest {
    private val connector = mockk<EventStoreConnector>()
    private val mapper = mockk<GameMapper>()
    private val repository = EventStoreGameRepository(connector, mapper)
    
    private val client = mockk<EventStoreDBClient>()
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
        every { mapper.serialize(game.pendingEvents[0]) } returns String()
        every { client.appendToStream(game.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(game)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { mapper.serialize(game.pendingEvents[0]) }
        verify(exactly = 1) { client.appendToStream(game.id.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should save 'game-scheduled' event with initial version 0`() {
        // given
        val game = Game.scheduled(UUID.randomUUID(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID())
        every { mapper.serialize(game.pendingEvents[0]) } returns String()
        every { client.appendToStream(game.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(game)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { mapper.serialize(game.pendingEvents[0]) }
        verify(exactly = 1) { client.appendToStream(game.id.toString(), ofType(EventData::class)).get() }
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
        every { recordedEvent.eventData } returnsMany listOf(ByteArray(0), ByteArray(0))
        every { mapper.deserialize(ofType(String::class), ofType(ByteArray::class)) } returnsMany listOf(
            GameScheduled(
                leagueId,
                UUID.fromString("1e56a755-1134-4f17-94fe-e6f2abe8ec07"),
                UUID.fromString("bb47a873-78ed-4320-a3b9-c214e63c9f6e"),
                1622276383, aggregateId
            ),
            GamePlayed(
                leagueId,
                UUID.fromString("1e56a755-1134-4f17-94fe-e6f2abe8ec07"), 4, -23, 78,
                UUID.fromString("bb47a873-78ed-4320-a3b9-c214e63c9f6e"), 3, -35, -63,
                1611176383, aggregateId, 1
            )
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
}