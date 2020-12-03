package com.tm.rankme.storage.write.league

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tm.rankme.domain.league.LeagueCreated
import com.tm.rankme.storage.write.league.LeagueEventEmitter
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate

internal class LeagueEventEmitterTest {
    private val template: RabbitTemplate = mock()
    private val exchange: TopicExchange = mock()
    private val eventEmitter = LeagueEventEmitter(template, exchange)

    @Test
    internal fun `Should emit event`() {
        // given
        val event = LeagueCreated("Star Wars")
        // when
        eventEmitter.emit(event)
        // then
        verify(template).send(eq(exchange.name), eq(event.type), any())
    }
}