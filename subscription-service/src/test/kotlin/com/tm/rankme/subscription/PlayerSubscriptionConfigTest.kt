package com.tm.rankme.subscription

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.subscription.PlayerSubscriptionConfig.PlayerCreatedMessage
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Sinks
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class PlayerSubscriptionConfigTest {
    private val subscriptionConfig = PlayerSubscriptionConfig()

    @Test
    fun `Should consume player-created message`() {
        // given
        val sinkMock = mockk<Sinks.Many<PlayerCreated>>(relaxed = true)
        val outboundSlot = slot<PlayerCreated>()
        val inboundMessage = PlayerCreatedMessage(randomNanoId(), randomNanoId(), "Optimus Prime", 3500, 1500)
        // when
        subscriptionConfig.playerCreatedConsumer(sinkMock).accept(inboundMessage)
        // then
        verify(exactly = 1) { sinkMock.tryEmitNext(capture(outboundSlot)) }
        outboundSlot.captured.let {
            assertEquals(inboundMessage.id, it.id)
            assertEquals(inboundMessage.leagueId, it.leagueId)
            assertEquals(inboundMessage.name, it.name)
            assertEquals(inboundMessage.deviation, it.deviation)
            assertEquals(inboundMessage.rating, it.rating)
        }
    }

    @Test
    fun `Should return PlayerCreated sink`() {
        // when
        val playerCreatedSink = subscriptionConfig.playerCreatedSink()
        // then
        assertNotNull(playerCreatedSink)
    }

    @Test
    fun `Should return PlayerCreated flux`() {
        // given
        val sinkMock = mockk<Sinks.Many<PlayerCreated>>(relaxed = true)
        // when
        val playerCreatedFlux = subscriptionConfig.playerCreatedFlux(sinkMock)
        // then
        assertNotNull(playerCreatedFlux)
    }
}