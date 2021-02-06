package com.tm.rankme.api.query.player

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PlayerQueryIntegrationTest {
    @Autowired
    private lateinit var template: GraphQLTestTemplate
    @MockkBean(relaxed = true)
    private lateinit var rabbitTemplate: RabbitTemplate
    @MockkBean
    private lateinit var exchange: DirectExchange
    private val exchangeName = "test.rankme.api"

    @BeforeEach
    internal fun setUp() {
        every { exchange.name } returns exchangeName
    }

    @Test
    internal fun `Should return league`() {
        // given
        val leagueId = "f9f9d3e6-098d-4771-b713-27a2507faa32"
        every { rabbitTemplate.sendAndReceive(exchangeName, "GetPlayerQuery", ofType(Message::class)) } returns
            Message("""{"id":"$leagueId", "name":"Optimus Prime",
            "deviation":186, "rating":2481}""".toByteArray(), MessageProperties())

        val request = "graphql/get-player.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(leagueId, response.get("$.data.getPlayer.id"))
        assertEquals("Optimus Prime", response.get("$.data.getPlayer.name"))
        assertEquals("186", response.get("$.data.getPlayer.deviation"))
        assertEquals("2481", response.get("$.data.getPlayer.rating"))
    }
}