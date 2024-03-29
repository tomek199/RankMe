package com.tm.rankme.subscription

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.graphql.spring.boot.test.GraphQLTestSubscription
import com.tm.rankme.SubscriptionService
import com.tm.rankme.subscription.PlayerSubscriptionConfig.PlayerCreatedMessage
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
        val context = SpringApplicationBuilder(*getCompleteConfiguration(SubscriptionService::class.java))
            .run("--spring.cloud.function.definition=playerCreatedConsumer", "--spring.profiles.active=test")
        val inboundEvent = PlayerCreatedMessage(randomNanoId(), "XB3x5Vk5l4s5eD8D9Dhbh", "Optimus Prime", 350, 1500)
        val inboundMessage = MessageBuilder.createMessage(inboundEvent, MessageHeaders(emptyMap()))
        context.getBean(InputDestination::class.java).send(inboundMessage)
        val subscription = context.getBean(GraphQLTestSubscription::class.java)
        // when
        val response = subscription.start("graphql/subscription/player-created.graphql").awaitAndGetNextResponse(2000)
        // then
        assertTrue(response.isOk)
        assertEquals(inboundEvent.id, response.get("data.playerCreated.id"))
        assertEquals(inboundEvent.name, response.get("data.playerCreated.name"))
        assertEquals(inboundEvent.deviation, response.get("data.playerCreated.deviation", Int::class.java))
        assertEquals(inboundEvent.rating, response.get("data.playerCreated.rating", Int::class.java))
    }
}