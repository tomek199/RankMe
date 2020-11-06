package com.tm.rankme.api.mutation

import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.tm.rankme.application.cqrs.Command
import com.tm.rankme.application.cqrs.CommandBus
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class CommandExecutorTest {
    private val commandBus: CommandBus = mock()
    private val command: Command = mock()
    private val executor = CommandExecutor(commandBus)

    @Test
    internal fun `Should execute command and return success result`() {
        // given
        doNothing().whenever(commandBus).execute(command)
        // when
        val result = executor.execute(command)
        // then
        assertEquals(Status.SUCCESS, result.status)
        assertNull(result.message)
    }

    @Test
    internal fun `Should catch execution exception and return error result`() {
        // given
        val exception = IllegalStateException("Command not supported")
        given(commandBus.execute(command)).willThrow(exception)
        // when
        val result = executor.execute(command)
        // then
        assertEquals(Status.FAILURE, result.status)
        assertEquals(exception.message, result.message)
    }
}