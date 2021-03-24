package com.tm.rankme.infrastructure

import com.tm.rankme.domain.league.LeagueCreated
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.GenericMessage

internal class SpringCloudEventBusTest {
    private val streamBridge = mockk<StreamBridge>()
    private val eventEmitter = SpringCloudEventBus(streamBridge)

    @Test
    internal fun `Should emit event`() {
        // given
        val exchangeName = "commandQuery-out-0"
        every { streamBridge.send(exchangeName, ofType(GenericMessage::class)) } returns true
        val event = LeagueCreated("Star Wars")
        // when
        eventEmitter.emit(event)
        // then
        verify(exactly = 1) { streamBridge.send(exchangeName, ofType(GenericMessage::class))}
    }
}