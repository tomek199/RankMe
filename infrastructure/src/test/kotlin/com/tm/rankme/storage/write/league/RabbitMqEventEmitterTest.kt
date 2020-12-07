package com.tm.rankme.storage.write.league

import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.storage.write.RabbitMqEventEmitter
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate

internal class RabbitMqEventEmitterTest {
    private val template = mockk<RabbitTemplate>(relaxed = true)
    private val exchange = mockk<TopicExchange>(relaxed = true)
    private val eventEmitter = RabbitMqEventEmitter(template, exchange)

    @Test
    internal fun `Should emit event`() {
        // given
        val event = LeagueCreated("Star Wars")
        // when
        eventEmitter.emit(event)
        // then
        verify(exactly = 1) { template.send(exchange.name, event.type, ofType(Message::class))}
    }
}