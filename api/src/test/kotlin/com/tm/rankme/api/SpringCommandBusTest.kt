package com.tm.rankme.api

import com.tm.rankme.api.mutation.SpringCommandBus
import com.tm.rankme.application.cqrs.Command
import com.tm.rankme.application.cqrs.CommandBus
import com.tm.rankme.application.cqrs.CommandHandler
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component

@SpringBootTest(classes = [SpringCommandBus::class, TestHandler::class])
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
}

@Component
private class TestHandler : CommandHandler<TestCommand> {
    override fun dispatch(command: TestCommand ) {
        command.testProperty = "Value changed"
    }
}

private data class TestCommand(var testProperty: String) : Command()