package com.tm.rankme.infrastructure

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class MessagingConfigurationTest {
    private val configuration = MessagingConfiguration()

    @Test
    internal fun `Should return json message converter`() {
        // when
        val converter = configuration.jsonMessageConverter()
        // then
        assertNotNull(converter)
    }

    @Test
    internal fun `Should return direct api exchange`() {
        // given
        val name = "rankme.api"
        // when
        val exchange = configuration.apiExchange()
        // then
        assertEquals(name, exchange.name)
    }

    @Test
    internal fun `Should return topic exchange`() {
        // given
        val name = "rankme"
        // when
        val exchange = configuration.exchange()
        // then
        assertEquals(name, exchange.name)
    }
}