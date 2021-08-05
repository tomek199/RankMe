package com.tm.rankme.infrastructure.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.eventstore.dbclient.*
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.domain.game.Result
import com.tm.rankme.infrastructure.EventStoreConnector
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
            randomNanoId(), randomNanoId(), randomNanoId(),
            Result(0, 184, -34, 2443, -176),
            Result(1, 194, -42, 2159, 148)
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
        val game = Game.scheduled(randomNanoId(), LocalDateTime.now(), randomNanoId(), randomNanoId())
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
        val aggregateId = randomNanoId()
        val leagueId = randomNanoId()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get().events } returns
            listOf(resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returnsMany listOf("game-scheduled", "game-played")
        every { recordedEvent.eventData } returnsMany listOf(ByteArray(0), ByteArray(0))
        every { mapper.deserialize(ofType(String::class), ofType(ByteArray::class)) } returnsMany listOf(
            GameScheduled(
                leagueId,
                "kv9s86MpG7zKfpnIC-O0H",
                "yHZuZbwbnsOwdqGfp5wBk",
                1622276383, aggregateId
            ),
            GamePlayed(
                leagueId,
                "kv9s86MpG7zKfpnIC-O0H", 4, 312, -23, 1674, 78,
                "yHZuZbwbnsOwdqGfp5wBk", 3, 250, -35, 2450, -63,
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
        assertEquals("kv9s86MpG7zKfpnIC-O0H", game.playerIds.first)
        assertEquals("yHZuZbwbnsOwdqGfp5wBk", game.playerIds.second)
        assertEquals(Result(4, 312, -23, 1674, 78), game.result!!.first)
        assertEquals(Result(3, 250, -35, 2450, -63), game.result!!.second)
    }
}