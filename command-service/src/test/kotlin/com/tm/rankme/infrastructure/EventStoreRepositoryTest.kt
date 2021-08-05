package com.tm.rankme.infrastructure

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.eventstore.dbclient.*
import com.tm.rankme.domain.base.Event
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class EventStoreRepositoryTest {
    private val connector = mockk<EventStoreConnector>()
    private val repository = object : EventStoreRepository<Any>(connector) {
        fun store(event: Event<Any>) = save(event)
        fun byId(stream: String) = events(stream)
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
        val exception = assertFailsWith<InfrastructureException> { repository.store(event) }
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
        val exception = assertFailsWith<InfrastructureException> { repository.store(event) }
        // then
        assertEquals("Version mismatch of aggregate id=${event.aggregateId}", exception.message)
    }

    @Test
    internal fun `Should throw exception when stream is not found`() {
        // given
        val aggregateId = randomNanoId()
        every { client.readStream(aggregateId.toString(), ofType(ReadStreamOptions::class)).get() } throws
            ExecutionException("Stream not found exception", StreamNotFoundException())
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.byId(aggregateId.toString()) }
        // then
        assertEquals("Stream $aggregateId is not found", exception.message)
    }

    private class TestEvent(version: Long = 1) : Event<Any>(randomNanoId(), version) {
        override val type: String = "test-event"
        override fun apply(aggregate: Any) { }
    }
}

