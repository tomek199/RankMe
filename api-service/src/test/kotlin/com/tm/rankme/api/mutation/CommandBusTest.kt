package com.tm.rankme.api.mutation

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.GenericMessage
import java.time.LocalDateTime
import java.util.*

internal class CommandBusTest {
    private val streamBridge = mockk<StreamBridge>()
    private val commandBus = CommandBus(streamBridge)
    private val bindingName = "apiCommand-out-0"

    @BeforeEach
    internal fun setUp() {
        every { streamBridge.send(bindingName, ofType(GenericMessage::class)) } returns true
    }

    @TestFactory
    internal fun `Should execute commands`() =
        // given
        listOf(
            CreateLeagueCommand("Star Wars"),
            RenameLeagueCommand(UUID.randomUUID(), "Transformers"),
            ChangeLeagueSettingsCommand(UUID.randomUUID(), true, 5),
            CreatePlayerCommand(UUID.randomUUID(), "Optimus Prime"),
            PlayGameCommand(UUID.randomUUID(), UUID.randomUUID(), 3, 2),
            ScheduleGameCommand(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now()),
            CompleteGameCommand(UUID.randomUUID(), 1, 4)
        ).map { command -> DynamicTest.dynamicTest("Given $command should be executed") {
            // when
            commandBus.execute(command)
            // then
            verify { streamBridge.send(bindingName, ofType(GenericMessage::class)) }
        }
    }
}