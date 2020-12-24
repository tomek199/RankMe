package com.tm.rankme.storage.write

import com.eventstore.dbclient.Connections
import com.eventstore.dbclient.EventStoreDBConnection
import com.eventstore.dbclient.Streams
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class EventStoreConnector(
    environment: Environment
) {
    final val stream: Streams

    init {
        val host = environment.getProperty("eventstore.host", "localhost")
        val port = environment.getProperty("eventstore.port", "2113").toInt()
        val connection: EventStoreDBConnection = Connections.builder().createSingleNodeConnection(host, port)
        stream = Streams.create(connection)
    }
}