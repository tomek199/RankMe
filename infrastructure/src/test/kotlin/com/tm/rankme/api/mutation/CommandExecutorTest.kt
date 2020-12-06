package com.tm.rankme.api.mutation

import com.tm.rankme.cqrs.command.Command
import com.tm.rankme.cqrs.command.CommandBus
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class CommandExecutorTest {
    private val commandBus = mockk<CommandBus>()
    private val command = mockk<Command>()
    private val executor = CommandExecutor(commandBus)

    @Test
    internal fun `Should execute command and return success result`() {
        // given
        every { commandBus.execute(command) } answers { nothing }
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
        every { commandBus.execute(command) } throws exception
        // when
        val result = executor.execute(command)
        // then
        assertEquals(Status.FAILURE, result.status)
        assertEquals(exception.message, result.message)
    }
}