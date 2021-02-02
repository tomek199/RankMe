package com.tm.rankme.api

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class MessagingConfigurationTest {
    private val configuration = MessagingConfiguration()

    @Test
    internal fun `Should return direct exchange`() {
        // given
        val name = "rankme.api"
        // when
        val exchange = configuration.exchange()
        // then
        assertEquals(name, exchange.name)
    }
}