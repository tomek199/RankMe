package com.tm.rankme.api.subscription.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.graphql.spring.boot.test.GraphQLTestSubscription
import com.tm.rankme.api.ApiService
import com.tm.rankme.api.subscription.player.PlayerSubscriptionConfig.PlayerCreatedMessage
import org.junit.jupiter.api.Test
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration.getCompleteConfiguration
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class PlayerSubscriptionResolverIntegrationTest {
    @Test
    fun `Should consume message from player-created subscription`() {
        // given
        val context = SpringApplicationBuilder(*getCompleteConfiguration(ApiService::class.java))
            .run("--spring.cloud.function.definition=playerCreatedFlux", "--spring.profiles.active=test")
        val inboundEvent = PlayerCreatedMessage(randomNanoId(), randomNanoId(), "Optimus Prime", 350, 1500)
        val inboundMessage = MessageBuilder.createMessage(inboundEvent, MessageHeaders(emptyMap()))
        context.getBean(InputDestination::class.java).send(inboundMessage)
        val subscription = context.getBean(GraphQLTestSubscription::class.java)
        // when
        val response = subscription.start("graphql/subscription/player-created.graphql").awaitAndGetNextResponse(1000)
        // then
        assertTrue(response.isOk)
        assertEquals(inboundEvent.aggregateId, response.get("data.playerCreated.id"))
        assertEquals(inboundEvent.name, response.get("data.playerCreated.name"))
        assertEquals(inboundEvent.deviation, response.get("data.playerCreated.deviation", Int::class.java))
        assertEquals(inboundEvent.rating, response.get("data.playerCreated.rating", Int::class.java))
    }
}