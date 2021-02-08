package com.tm.rankme.infrastructure

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Profile("eventstore")
@Component
class EventStoreConnector(
    environment: Environment
) {
    final val client: EventStoreDBClient

    init {
        val endpoint = environment.getProperty("eventstore.endpoint", "esdb://localhost:2113")
        val settings: EventStoreDBClientSettings = EventStoreDBConnectionString.parseOrThrow(endpoint)
        client = EventStoreDBClient.create(settings)
    }
}