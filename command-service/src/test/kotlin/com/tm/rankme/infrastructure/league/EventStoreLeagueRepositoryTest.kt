package com.tm.rankme.infrastructure.league

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamRevision
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.domain.league.LeagueRenamed
import com.tm.rankme.domain.league.LeagueSettingsChanged
import com.tm.rankme.infrastructure.EventStoreConnector
import com.tm.rankme.infrastructure.InfrastructureException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventStoreLeagueRepositoryTest {
    private val connector = mockk<EventStoreConnector>()
    private val mapper = mockk<LeagueMapper>()
    private val repository = EventStoreLeagueRepository(connector, mapper)

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
        every { mapper.serialize(league.pendingEvents[0]) } returns String()
        every { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(league)
        // then
        verify(exactly = 0) { client.readStream(any()) }
        verify(exactly = 1) { mapper.serialize(league.pendingEvents[0]) }
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
        every { mapper.serialize(league.pendingEvents[0]) } returns String()
        every { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(league)
        // then
        verify(exactly = 1) { client.readStream(league.id.toString(), 1, ofType(ReadStreamOptions::class)).get() }
        verify(exactly = 1) { mapper.serialize(league.pendingEvents[0]) }
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
        every { mapper.serialize(league.pendingEvents[0]) } returns String()
        every { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() } returns mockk()
        // when
        repository.store(league)
        // then
        verify(exactly = 1) { client.readStream(league.id.toString(), 1, ofType(ReadStreamOptions::class)).get() }
        verify(exactly = 1) { mapper.serialize(league.pendingEvents[0]) }
        verify(exactly = 1) { client.appendToStream(league.id.toString(), ofType(EventData::class)).get() }
    }

    @Test
    internal fun `Should return aggregate from events`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get().events } returns
            listOf(resolvedEvent, resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returnsMany listOf("league-created", "league-renamed", "league-settings-changed")
        every { recordedEvent.eventData } returnsMany listOf(ByteArray(0), ByteArray(0), ByteArray(0))
        every { mapper.deserialize(ofType(String::class), ofType(ByteArray::class)) } returnsMany listOf(
            LeagueCreated("Star Wars", false, 2, aggregateId),
            LeagueRenamed(aggregateId, 1, "Transformers"),
            LeagueSettingsChanged(aggregateId, 2, true, 10)
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
    internal fun `Should return true when league exist`() {
        // given
        val leagueId = UUID.randomUUID()
        every { client.readStream(leagueId.toString(), ofType(ReadStreamOptions::class)).get().events } returns
            listOf(resolvedEvent, resolvedEvent, resolvedEvent)
        every { resolvedEvent.originalEvent } returns recordedEvent
        every { recordedEvent.eventType } returnsMany listOf("league-created")
        every { recordedEvent.eventData } returnsMany listOf(ByteArray(0))
        every { mapper.deserialize(ofType(String::class), ofType(ByteArray::class)) } returns LeagueCreated("Star Wars")
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