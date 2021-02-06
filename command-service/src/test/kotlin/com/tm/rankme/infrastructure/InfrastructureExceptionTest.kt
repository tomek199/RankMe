package com.tm.rankme.infrastructure

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class InfrastructureExceptionTest {
    @Test
    internal fun `Should create exception with message`() {
        // given
        val message = "Event is not found"
        // when
        val exception = InfrastructureException(message)
        // then
        assertEquals(message, exception.message)
    }

    @Test
    internal fun `Should create exception with message and cause`() {
        // given
        val parent = IllegalStateException("Event is not found")
        // when
        val exception = InfrastructureException(parent.message, parent)
        // then
        assertEquals(parent.message, exception.message)
        assertEquals(parent, exception.cause)
    }
}