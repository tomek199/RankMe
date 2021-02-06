package com.tm.rankme.api.query.league

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
internal class LeagueQueryIntegrationTest {
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
        val leagueId = "83222ad3-219c-48b0-bcc2-817efb61cfda"
        every { rabbitTemplate.sendAndReceive(exchangeName, "GetLeagueQuery", ofType(Message::class)) } returns
            Message("""{"id":"$leagueId", "name":"Transformers",
            "allowDraws":true, "maxScore":3}""".toByteArray(), MessageProperties())

        val request = "graphql/get-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(leagueId, response.get("$.data.getLeague.id"))
        assertEquals("Transformers", response.get("$.data.getLeague.name"))
        assertEquals("true", response.get("$.data.getLeague.allowDraws"))
        assertEquals("3", response.get("$.data.getLeague.maxScore"))
    }
}