package com.tm.rankme.storage.write

import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamNotFoundException
import com.eventstore.dbclient.StreamRevision
import com.eventstore.dbclient.Streams
import com.tm.rankme.domain.base.AggregateRoot
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
    private val eventStorage = object : EsEventStorage<TestAggregate>(connector) {
        override fun deserialize(recordedEvent: RecordedEvent): Event<TestAggregate> {
            throw InfrastructureException("Cannot deserialize event")
        }
        override fun serialize(event: Event<TestAggregate>): Any {
            return if (event.type == "test-event") Any() else throw InfrastructureException("Cannot serialize event")
        }
    }

    private val streams = mockk<Streams>()
    private val readResult = mockk<ReadResult>()
    private val resolvedEvent = mockk<ResolvedEvent>()
    private val recordedEvent = mockk<RecordedEvent>()

    @BeforeEach
    internal fun setUp() {
        every { connector.stream } returns streams
    }

    @Test
    internal fun `Should throw exception when cannot get actual aggregate version`() {
        // given
        val event = TestEvent(1)
        every { streams.readStream(event.aggregateId.toString()).fromEnd().backward().execute(1).get() } returns readResult
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
        every { streams.readStream(event.aggregateId.toString()).fromEnd().backward().execute(1).get() } returns readResult
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
        every { streams.readStream(aggregateId.toString()).fromStart().readThrough().get() } throws
            ExecutionException("Stream not found exception", StreamNotFoundException())
        // when
        val exception = assertFailsWith<InfrastructureException> { eventStorage.events(aggregateId.toString()) }
        // then
        assertEquals("Stream $aggregateId is not found", exception.message)
    }

    private class TestEvent(version: Long = 1) : Event<TestAggregate>(UUID.randomUUID(), version) {
        override val type: String = "test-event"
        override fun apply(aggregate: TestAggregate) { }
    }

    private class TestAggregate : AggregateRoot()
}

