package com.tm.rankme.command

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

internal class CommandHandlerTest {
    @Test
    internal fun `Should have access to event bus`() {
        // given
        val commandHandler = object : CommandHandler<TestCommand>(mockk()) {
            override fun execute(command: TestCommand): List<Event<out AggregateRoot>> = emptyList()
            fun eventBus() = eventBus
        }
        // when
        val eventBus = commandHandler.eventBus()
        // then
        assertNotNull(eventBus)
    }

    data class TestCommand(val test: String) : Command()
}