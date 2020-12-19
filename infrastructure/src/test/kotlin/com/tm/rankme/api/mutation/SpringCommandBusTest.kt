package com.tm.rankme.api.mutation

import com.tm.rankme.cqrs.command.Command
import com.tm.rankme.cqrs.command.CommandBus
import com.tm.rankme.cqrs.command.CommandHandler
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component

@SpringBootTest(classes = [SpringCommandBus::class, TestCommandHandler::class])
internal class SpringCommandBusTest(@Autowired private val commandBus: CommandBus) {
    @Test
    internal fun `Should execute command`() {
        // given
        val expectedValue = "Value changed"
        val command: Command = TestCommand("Initial value")
        // when
        commandBus.execute(command)
        // then
        assertEquals(expectedValue, (command as TestCommand).testProperty)
    }

    @Test
    internal fun `Should throw exception when cannot find handler for command`() {
        // given
        val command: Command = CommandWithoutHandler("Initial value")
        // when
        val exception = assertFailsWith<IllegalStateException> { commandBus.execute(command) }
        // then
        assertEquals("Command type not found", exception.message)
    }
}

@Component
private class TestCommandHandler : CommandHandler<TestCommand> {
    override fun dispatch(command: TestCommand) {
        command.testProperty = "Value changed"
    }
}

private data class TestCommand(var testProperty: String) : Command()
private data class CommandWithoutHandler(var testProperty: String) : Command()