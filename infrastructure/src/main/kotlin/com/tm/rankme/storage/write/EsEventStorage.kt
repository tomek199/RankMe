package com.tm.rankme.storage.write

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.RecordedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventStorage
import java.util.concurrent.ExecutionException
import org.slf4j.LoggerFactory

abstract class EsEventStorage<T>(
    private val connector: EventStoreConnector
) : EventStorage<T> {

    private val log = LoggerFactory.getLogger(EsEventStorage::class.java)
    protected val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun save(event: Event<T>) {
        log.info("Saving event ${event.type} for aggregate ${event.aggregateId}")
        checkVersion(event)
        addToStream(event)
    }

    private fun checkVersion(event: Event<T>) {
        if (event.version != 0L) {
            val readOptions = ReadStreamOptions.get().fromEnd().backwards()
            val readResult = connector.client.readStream(event.aggregateId.toString(), 1, readOptions).get()
            val currentVersion = readResult.events.getOrElse(0) {
                throw InfrastructureException("Cannon get actual version of aggregate id=${event.aggregateId}")
            }.event.streamRevision
            if (currentVersion.valueUnsigned != event.version - 1)
                throw InfrastructureException("Version mismatch of aggregate id=${event.aggregateId}")
        }
    }

    private fun addToStream(event: Event<T>) {
        val eventData = EventData.builderAsJson(event.type, serialize(event)).build()
        connector.client.appendToStream(event.aggregateId.toString(), eventData).get()
    }

    override fun events(stream: String): List<Event<T>> {
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
