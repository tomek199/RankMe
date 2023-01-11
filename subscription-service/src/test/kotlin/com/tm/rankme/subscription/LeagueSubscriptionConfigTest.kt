package com.tm.rankme.subscription

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.subscription.LeagueSubscriptionConfig.LeagueCreatedMessage
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Sinks
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LeagueSubscriptionConfigTest {
    private val subscriptionConfig = LeagueSubscriptionConfig()

    @Test
    fun `Should consume league-created message`() {
        // given
        val sinkMock = mockk<Sinks.Many<LeagueCreated>>(relaxed = true)
        val outboundSlot = slot<LeagueCreated>()
        val inboundMessage = LeagueCreatedMessage(randomNanoId(), "Star Wars", true, 8)
        // when
        subscriptionConfig.leagueCreatedConsumer(sinkMock).accept(inboundMessage)
        // then
        verify(exactly = 1) { sinkMock.tryEmitNext(capture(outboundSlot)) }
        outboundSlot.captured.let {
            assertEquals(inboundMessage.id, it.id)
            assertEquals(inboundMessage.name, it.name)
            assertEquals(inboundMessage.allowDraws, it.allowDraws)
            assertEquals(inboundMessage.maxScore, it.maxScore)
        }
    }

    @Test
    fun `Should return LeagueCreatedSink`() {
        // when
        val leagueCreatedSink = subscriptionConfig.leagueCreatedSink()
        // then
        assertNotNull(leagueCreatedSink)
    }

    @Test
    fun `Should return LeagueCreated flux`() {
        // given
        val sinkMock = mockk<Sinks.Many<LeagueCreated>>(relaxed = true)
        // when
        val leagueCreatedFlux = subscriptionConfig.leagueCreatedFlux(sinkMock)
        // then
        assertNotNull(leagueCreatedFlux)
    }
}