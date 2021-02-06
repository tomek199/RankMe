package com.tm.rankme.api.query

import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate

internal class QueryBusTest {
    private val template = mockk<RabbitTemplate>()
    private val exchange = mockk<DirectExchange>()
    private val bus = QueryBus(template, exchange)
    private val exchangeName = "rankme.test.api"
    private val mapper = mockk<Mapper<TestResponse>>()

    @BeforeEach
    internal fun setUp() {
        every { exchange.name } returns exchangeName
    }

    @Test
    internal fun `Should execute query and return result`() {
        // given
        val messageBytes = """{"testField":"Test value"}""".toByteArray()
        every { template.sendAndReceive(exchangeName, "TestQuery", ofType(Message::class)) } returns
            Message(messageBytes, MessageProperties())
        every { mapper.deserialize(messageBytes) } returns TestResponse("Test value")
        // when
        val response = bus.execute(TestQuery(), mapper)
        // then
        assertNotNull(response)
        assertEquals(response.testField, "Test value")
    }

    @Test
    internal fun `Should execute query and return null`() {
        // given
        every { template.sendAndReceive(exchangeName, "TestQuery", ofType(Message::class)) } returns null
        // when
        val response = bus.execute(TestQuery(), mapper)
        // then
        assertNull(response)
    }

    private class TestQuery : Query()
    private data class TestResponse(val testField: String)
}