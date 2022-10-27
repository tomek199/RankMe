package com.tm.rankme.mutation

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.GenericMessage
import java.time.LocalDateTime

internal class CommandBusTest {
    private val streamBridge = mockk<StreamBridge>()
    private val commandBus = CommandBus(streamBridge)
    private val bindingName = "commands-out-0"

    @BeforeEach
    internal fun setUp() {
        every { streamBridge.send(bindingName, ofType(GenericMessage::class)) } returns true
    }

    @TestFactory
    internal fun `Should execute commands`() =
        // given
        listOf(
            CreateLeagueCommand("Star Wars"),
            RenameLeagueCommand(randomNanoId(), "Transformers"),
            ChangeLeagueSettingsCommand(randomNanoId(), true, 5),
            CreatePlayerCommand(randomNanoId(), "Optimus Prime"),
            PlayGameCommand(randomNanoId(), randomNanoId(), 3, 2),
            ScheduleGameCommand(randomNanoId(), randomNanoId(), LocalDateTime.now()),
            CompleteGameCommand(randomNanoId(), 1, 4)
        ).map { command -> DynamicTest.dynamicTest("Given $command should be executed") {
            // when
            commandBus.execute(command)
            // then
            verify { streamBridge.send(bindingName, ofType(GenericMessage::class)) }
        }
    }
}