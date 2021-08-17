package com.tm.rankme.infrastructure

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.RecordedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutionException

abstract class EventStoreRepository<T>(
    private val connector: EventStoreConnector
) {

    private val log = LoggerFactory.getLogger(EventStoreRepository::class.java)
    protected val objectMapper: ObjectMapper = jacksonObjectMapper()

    protected fun save(event: Event<T>) {
        log.info("Saving event ${event.type} for aggregate ${event.aggregateId}")
        checkVersion(event)
        addToStream(event)
    }

    private fun checkVersion(event: Event<T>) {
        if (event.version != 0L) {
            val readOptions = ReadStreamOptions.get().fromEnd().backwards()
            val readResult = connector.client.readStream(event.aggregateId, 1, readOptions).get()
            val currentVersion = readResult.events.getOrElse(0) {
                throw InfrastructureException("Cannon get actual version of aggregate id=${event.aggregateId}")
            }.event.streamRevision
            if (currentVersion.valueUnsigned != event.version - 1)
                throw InfrastructureException("Version mismatch of aggregate id=${event.aggregateId}")
        }
    }

    private fun addToStream(event: Event<T>) {
        val eventData = EventData.builderAsJson(event.type, serialize(event)).build()
        connector.client.appendToStream(event.aggregateId, eventData).get()
    }

    protected fun events(stream: String): List<Event<T>> {
        log.info("Getting events for stream $stream")
        try {
            val readOptions = ReadStreamOptions.get().fromStart().forwards()
            return connector.client.readStream(stream, readOptions).get().events.map {
                deserialize(it.originalEvent)
            }.toList()
        } catch (e: ExecutionException) {
            throw InfrastructureException("Stream $stream is not found", e)
        }
    }

    protected abstract fun serialize(event: Event<T>): Any
    protected abstract fun deserialize(recordedEvent: RecordedEvent): Event<T>
}
