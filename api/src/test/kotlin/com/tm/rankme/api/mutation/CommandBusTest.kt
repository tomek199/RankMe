package com.tm.rankme.api.mutation

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate

internal class CommandBusTest {
    private val template = mockk<RabbitTemplate>()
    private val exchange = mockk<DirectExchange>()
    private val eventBus = CommandBus(template, exchange)
    private val exchangeName = "rankme.test.api"

    @BeforeEach
    internal fun setUp() {
        every { exchange.name } returns exchangeName
        every { template.send(exchangeName, ofType(String::class), ofType(Message::class)) } just Runs
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
            eventBus.execute(command)
            // then
            verify { template.send(exchangeName, command::class.simpleName!!, ofType(Message::class)) }
        }
    }
}