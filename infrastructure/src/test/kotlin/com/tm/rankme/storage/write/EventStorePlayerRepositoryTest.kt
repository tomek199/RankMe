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
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventStorePlayerRepositoryTest {
    private val connector = mockk<EventStoreConnector>()
    private val repository = EventStorePlayerRepository(connector)

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
        val player = Player.create(UUID.randomUUID(), "Optimus Prime")
        every { client.appendToStream(player.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(player)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { client.appendToStream(player.id.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should save 'played-game' event with version 1`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOne = Player.from(listOf(PlayerCreated(leagueId, "Batman")))
        val playerTwo = Player.from(listOf(PlayerCreated(leagueId, "Superman")))
        playerOne.playedWith(playerTwo, 2, 0)
        every { client.readStream(playerOne.id.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        every { client.appendToStream(playerOne.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(playerOne)
        // then
        verify(exactly = 1) { client.readStream(playerOne.id.toString(), 1, ofType(ReadStreamOptions::class)).get() }
        verify(exactly = 1) { client.appendToStream(playerOne.id.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should throw exception when cannot serialize event`() {
        // given
        val player = Player.from(listOf(PlayerCreated(UUID.randomUUID(), "Optimus Prime")))
        val event = object : Event<Player>(player.id, 1) {
            override val type: String = "unknown-event"
            override fun apply(aggregate: Player) { }
        }
        player.pendingEvents.add(event)
        every { client.readStream(player.id.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.store(player) }
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
        every { recordedEvent.eventType } returnsMany listOf("player-created", "player-played-game")
        every { recordedEvent.eventData } returnsMany listOf(
            """{"type": "player-created", "aggregateId": "$aggregateId", "version": 0, "timestamp": 0, 
            "leagueId": "$leagueId", "name": "Optimus Prime", "deviation": 149, "rating": 2859}""".toByteArray(),
            """{"type": "player-played-game", "aggregateId": "$aggregateId", "version": 1, "timestamp": 0, 
            "deviationDelta": -36, "ratingDelta": -132, "score": 2}""".toByteArray(),
        )
        // when
        val player = repository.byId(aggregateId)
        // then
        assertEquals(aggregateId, player.id)
        assertEquals(1, player.version)
        assertTrue(player.pendingEvents.isEmpty())
        assertEquals(leagueId, player.leagueId)
        assertEquals("Optimus Prime", player.name)
        assertEquals(113, player.deviation)
        assertEquals(2727, player.rating)
    }

    @Test
    internal fun `Should throw exception when cannot deserialize invalid event json`() {
        // given
        val events = listOf("player-created", "player-played-game")
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
    internal fun `Should throw exception when cannot deserialize unknown event`() {
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