package com.tm.rankme.storage.write

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamNotFoundException
import com.eventstore.dbclient.StreamRevision
import com.tm.rankme.domain.base.Event
import io.mockk.every
import io.mockk.mockk
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EsEventStorageTest {
    private val connector = mockk<EventStoreConnector>()
    private val eventStorage = object : EsEventStorage<Any>(connector) {
        override fun deserialize(recordedEvent: RecordedEvent): Event<Any> {
            throw InfrastructureException("Cannot deserialize event")
        }
        override fun serialize(event: Event<Any>): Any {
            throw InfrastructureException("Cannot serialize event")
        }
    }

    private val client = mockk<EventStoreDBClient>()
    private val readResult = mockk<ReadResult>()
    private val resolvedEvent = mockk<ResolvedEvent>()

    @BeforeEach
    internal fun setUp() {
        every { connector.client } returns client
    }

    @Test
    internal fun `Should throw exception when cannot get actual aggregate version`() {
        // given
        val event = TestEvent(1)
        every { client.readStream(event.aggregateId.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { resolvedEvent.event.streamRevision } returns StreamRevision(0)
        every { readResult.events } returns emptyList()
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.save(event) }
        // then
        assertEquals("Cannon get actual version of aggregate id=${event.aggregateId}", exception.message)
    }

    @Test
    internal fun `Should throw exception when event version is out of date`() {
        // given
        val event = TestEvent(1)
        every { client.readStream(event.aggregateId.toString(), 1, ofType(ReadStreamOptions::class)).get() } returns readResult
        every { readResult.events } returns listOf(resolvedEvent)
        every { resolvedEvent.event.streamRevision } returns StreamRevision(15)
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.save(event) }
        // then
        assertEquals("Version mismatch of aggregate id=${event.aggregateId}", exception.message)
    }

    @Test
    internal fun `Should throw exception when stream is not found`() {
        // given
        val aggregateId = UUID.randomUUID()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get() } throws
            ExecutionException("Stream not found exception", StreamNotFoundException())
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.events(aggregateId.toString()) }
        // then
        assertEquals("Stream $aggregateId is not found", exception.message)
    }

    private class TestEvent(version: Long = 1) : Event<Any>(UUID.randomUUID(), version) {
        override val type: String = "test-event"
        override fun apply(aggregate: Any) { }
    }
}

