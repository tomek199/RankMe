package com.tm.rankme.storage.write

import com.eventstore.dbclient.Client
import com.eventstore.dbclient.ClientSettings
import com.eventstore.dbclient.ConnectionString
import com.eventstore.dbclient.Streams
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class EventStoreConnector(
    environment: Environment
) {
    final val stream: Streams

    init {
        val endpoint = environment.getProperty("eventstore.endpoint", "esdb://localhost:2113")
        val settings: ClientSettings = ConnectionString.parseOrThrow(endpoint)
        val client = Client.create(settings)
        stream = client.streams()
    }
}