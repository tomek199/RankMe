package com.tm.rankme.subscription

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.graphql.spring.boot.test.GraphQLTestSubscription
import com.tm.rankme.SubscriptionService
import com.tm.rankme.subscription.LeagueSubscriptionConfig.LeagueCreatedMessage
import org.junit.jupiter.api.Test
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class LeagueSubscriptionResolverIntegrationTest {
    @Test
    fun `Should consume message from league-created subscription`() {
        // given
        val context = SpringApplicationBuilder(*TestChannelBinderConfiguration.getCompleteConfiguration(SubscriptionService::class.java))
            .run("--spring.cloud.function.definition=leagueCreatedConsumer", "--spring.profiles.active=test")
        val inboundEvent = LeagueCreatedMessage(randomNanoId(), "Transformers", true, 5)
        val inboundMessage = MessageBuilder.createMessage(inboundEvent, MessageHeaders(emptyMap()))
        context.getBean(InputDestination::class.java).send(inboundMessage)
        val subscription = context.getBean(GraphQLTestSubscription::class.java)
        // when
        val response = subscription.start("graphql/subscription/league-created.graphql").awaitAndGetNextResponse(2000)
        // then
        assertTrue(response.isOk)
        assertEquals(inboundEvent.id, response.get("data.leagueCreated.id"))
        assertEquals(inboundEvent.name, response.get("data.leagueCreated.name"))
        assertEquals(inboundEvent.allowDraws, response.get("data.leagueCreated.allowDraws", Boolean::class.java))
        assertEquals(inboundEvent.maxScore, response.get("data.leagueCreated.maxScore", Int::class.java))
    }
}