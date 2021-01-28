package com.tm.rankme.cqrs.command

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import io.mockk.mockk
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

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