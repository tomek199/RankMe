package com.tm.rankme.storage.write

import com.eventstore.dbclient.ProposedEvent
import com.eventstore.dbclient.RecordedEvent
import com.tm.rankme.domain.base.Event
import java.util.concurrent.ExecutionException

abstract class EsEventStorage<T>(
    private val eventStoreConnector: EventStoreConnector
) : EventStorage<T> {

    override fun save(event: Event<T>) {
        checkVersion(event)
        addToStream(event)
    }

    private fun checkVersion(event: Event<T>) {
        if (event.version != 0L) {
            val readResult = eventStoreConnector.stream.readStream(event.aggregateId.toString())
                .fromEnd().backward().execute(1).get()
            val currentVersion = readResult.events.getOrElse(0) {
                throw InfrastructureException("Cannon get actual version of aggregate id=${event.aggregateId}")
            }.event.streamRevision
            if (currentVersion.valueUnsigned != event.version - 1)
                throw InfrastructureException("Version mismatch of aggregate id=${event.aggregateId}")
        }
    }

    private fun addToStream(event: Event<T>) {
        val proposedEvent = ProposedEvent.builderAsJson(event.type, serialize(event)).build()
        eventStoreConnector.stream.appendStream(event.aggregateId.toString())
            .addEvent(proposedEvent).execute().get()
    }

    override fun events(stream: String): List<Event<T>> {
        try {
            return eventStoreConnector.stream.readStream(stream)
                .fromStart().readThrough().get().events.map { deserialize(it.originalEvent) }.toList()
        } catch (e: ExecutionException) {
            throw InfrastructureException("Stream $stream is not found", e)
        }
    }

    protected abstract fun deserialize(recordedEvent: RecordedEvent): Event<T>

    protected abstract fun serialize(event: Event<T>): Any
}
