package com.tm.rankme.api.query.player

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestTemplate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PlayerQueryIntegrationTest {
    @Autowired
    private lateinit var template: GraphQLTestTemplate
    @Value("\${gateway.url}")
    private lateinit var url: String
    @MockkBean
    private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `Should return player`() {
        // given
        val player = Player(UUID.fromString("f9f9d3e6-098d-4771-b713-27a2507faa32"), "Optimus Prime", 186, 2481)
        every { restTemplate.getForObject("$url/query-service/players/${player.id}", Player::class.java) } returns player
        val request = "graphql/get-player.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(player.id.toString(), response.get("$.data.getPlayer.id"))
        assertEquals(player.name, response.get("$.data.getPlayer.name"))
        assertEquals(player.deviation, response.get("$.data.getPlayer.deviation", Int::class.java))
        assertEquals(player.rating, response.get("$.data.getPlayer.rating", Int::class.java))
    }
}