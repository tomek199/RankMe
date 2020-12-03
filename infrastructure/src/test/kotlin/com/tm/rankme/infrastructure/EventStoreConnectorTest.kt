package com.tm.rankme.infrastructure

import com.eventstore.dbclient.Streams
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlin.test.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.env.Environment

internal class EventStoreConnectorTest {
    private val environment: Environment = mock()
    private lateinit var connection: EventStoreConnector

    @BeforeEach
    internal fun setUp() {
        given(environment.getProperty("eventstore.host", "localhost")).willReturn("host")
        given(environment.getProperty("eventstore.port", "2113")).willReturn("1234")
    }

    @Test
    internal fun `Should create stream connection`() {
        // given
        connection = EventStoreConnector(environment)
        // when
        val stream: Streams = connection.stream
        // then
        verify(environment).getProperty("eventstore.host", "localhost")
        verify(environment).getProperty("eventstore.port", "2113")
        assertNotNull(stream)
    }
}