package com.tm.rankme.storage.write

import com.eventstore.dbclient.EventStoreDBClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.env.Environment

internal class EventStoreConnectorTest {
    private val environment = mockk<Environment>()
    private lateinit var connection: EventStoreConnector

    @BeforeEach
    internal fun setUp() {
        every { environment.getProperty("eventstore.endpoint", "esdb://localhost:2113") } returns "esdb://localhost:2113"
    }

    @Test
    internal fun `Should create stream connection`() {
        // given
        connection = EventStoreConnector(environment)
        // when
        val client: EventStoreDBClient = connection.client
        // then
        verify { environment.getProperty("eventstore.endpoint", "esdb://localhost:2113") }
        assertNotNull(client)
    }
}