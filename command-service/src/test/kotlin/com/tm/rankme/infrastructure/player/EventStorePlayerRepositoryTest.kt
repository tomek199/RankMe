package com.tm.rankme.infrastructure.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.eventstore.dbclient.*
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerPlayedGame
import com.tm.rankme.infrastructure.EventStoreConnector
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EventStorePlayerRepositoryTest {
    private val connector = mockk<EventStoreConnector>()
    private val mapper = mockk<PlayerMapper>()
    private val repository = EventStorePlayerRepository(connector, mapper)

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
        val player = Player.create(randomNanoId(), "Optimus Prime")
        every { mapper.serialize(player.pendingEvents[0]) } returns String()
        every { client.appendToStream(player.id, ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(player)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { mapper.serialize(player.pendingEvents[0]) }
        verify(exactly = 1) { client.appendToStream(player.id, ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should save 'played-game' event with version 1`() {
        // given
        val leagueId = randomNanoId()
        val playerOne = Player.from(listOf(PlayerCreated(leagueId, "Batman")))
        val playerTwo = Player.from(listOf(PlayerCreated(leagueId, "Superman")))
        playerOne.playedWith(playerTwo, 2, 0)
        every { client.readStream(playerOne.id, 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        every { mapper.serialize(playerOne.pendingEvents[0]) } returns String()
        every { client.appendToStream(playerOne.id, ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(playerOne)
        // then
        verify(exactly = 1) { client.readStream(playerOne.id, 1, ofType(ReadStreamOptions::class)).get() }
        verify(exactly = 1) { mapper.serialize(playerOne.pendingEvents[0]) }
        verify(exactly = 1) { client.appendToStream(playerOne.id, ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should return aggregate from events`() {
        // given
        val aggregateId = randomNanoId()
        val leagueId = randomNanoId()
        every { client.readStream(aggregateId, ofType(ReadStreamOptions::class)).get().events } returns
            listOf(resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returnsMany listOf("player-created", "player-played-game")
        every { recordedEvent.eventData } returnsMany listOf(ByteArray(0), ByteArray(0))
        every { mapper.deserialize(ofType(String::class), ofType(ByteArray::class)) } returnsMany listOf(
            PlayerCreated(leagueId, "Optimus Prime", 149, 2859, aggregateId),
            PlayerPlayedGame(-36, -132, 2, aggregateId, 1)
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
}