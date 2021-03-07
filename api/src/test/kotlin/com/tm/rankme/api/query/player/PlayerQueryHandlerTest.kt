package com.tm.rankme.api.query.player

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class PlayerQueryHandlerTest {
    private val restTemplate = mockk<RestTemplate>()
    private val url = "http://gateway"
    private val handler = PlayerQueryHandler(restTemplate, url)

    @Test
    internal fun `Should return player`() {
        // given
        val player = Player(UUID.randomUUID(), "Optimus Prime", 263, 2467)
        every { restTemplate.getForObject("$url/query-service/players/${player.id}", Player::class.java) } returns player
        val query = GetPlayerQuery(player.id)
        // when
        val result = handler.handle(query)
        // then
        assertNotNull(result)
        assertEquals(player.id, result.id)
        assertEquals(player.name, result.name)
        assertEquals(player.deviation, result.deviation)
        assertEquals(player.rating, result.rating)
    }

    @Test
    internal fun `Should return null for player`() {
        // given
        val query = GetPlayerQuery(UUID.randomUUID())
        every { restTemplate.getForObject("$url/query-service/players/${query.id}", Player::class.java) } returns null
        // when
        val result = handler.handle(query)
        // then
        assertNull(result)
    }
}