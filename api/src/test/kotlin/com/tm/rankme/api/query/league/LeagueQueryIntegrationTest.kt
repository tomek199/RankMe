package com.tm.rankme.api.query.league

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestTemplate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class LeagueQueryIntegrationTest {
    @Autowired
    private lateinit var template: GraphQLTestTemplate
    @MockkBean
    private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `Should return league`() {
        // given
        val league = League(UUID.fromString("83222ad3-219c-48b0-bcc2-817efb61cfda"), "Transformers", true, 3)
        every { restTemplate.getForObject(ofType(String::class), League::class.java) } returns league
        val request = "graphql/get-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(league.id.toString(), response.get("$.data.getLeague.id"))
        assertEquals(league.name, response.get("$.data.getLeague.name"))
        assertEquals(league.allowDraws, response.get("$.data.getLeague.allowDraws", Boolean::class.java))
        assertEquals(league.maxScore, response.get("$.data.getLeague.maxScore", Int::class.java))
    }
}