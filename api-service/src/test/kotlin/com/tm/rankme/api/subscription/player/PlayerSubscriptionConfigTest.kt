package com.tm.rankme.api.subscription.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class PlayerSubscriptionConfigTest {
    private val subscriptionConfig = PlayerSubscriptionConfig()

    @Test
    fun `Should return PlayerCreated sink`() {
        // when
        val playerCreatedSink = subscriptionConfig.playerCreatedSink()
        // then
        assertNotNull(playerCreatedSink)
    }

    @Test
    fun `Should return Player Created flux`() {
        // given
        val sinkMock = mockk<Sinks.Many<PlayerCreated>>(relaxed = true)
        val outboundSlot = slot<PlayerCreated>()
        val inboundMessage = PlayerSubscriptionConfig.PlayerCreatedMessage(randomNanoId(), randomNanoId(), "Optimus Prime", 3500, 1500)
        // when
        subscriptionConfig.playerCreatedFlux(sinkMock).accept(Flux.just(inboundMessage))
        // then
        verify(exactly = 1) { sinkMock.emitNext(capture(outboundSlot), FAIL_FAST) }
        outboundSlot.captured.let {
            assertEquals(inboundMessage.aggregateId, it.id)
            assertEquals(inboundMessage.name, it.name)
            assertEquals(inboundMessage.deviation, it.deviation)
            assertEquals(inboundMessage.rating, it.rating)
        }
    }
}