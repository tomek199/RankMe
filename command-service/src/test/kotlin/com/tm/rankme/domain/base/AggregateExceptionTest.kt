package com.tm.rankme.domain.base

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class AggregateExceptionTest {
    @Test
    internal fun `Should create exception with message`() {
        // given
        val message = "Something went wrong!"
        // when
        val exception = AggregateException(message)
        // then
        assertEquals(message, exception.message)
    }

    @Test
    internal fun `Should create exception with message and cause`() {
        // given
        val parent = IllegalStateException("Something went wrong")
        // when
        val exception = AggregateException(parent.message, parent)
        // then
        assertEquals(parent.message, exception.message)
        assertEquals(parent, exception.cause)
    }
}