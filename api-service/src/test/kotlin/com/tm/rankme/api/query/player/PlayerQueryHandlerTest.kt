package com.tm.rankme.api.query.player

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    @Test
    internal fun `Should return players list by league id`() {
        // given
        val leagueId = UUID.randomUUID()
        val players = listOf(
            Player(UUID.randomUUID(), "Optimus Prime", 246, 2519),
            Player(UUID.randomUUID(), "Bumblebee", 174, 1864)
        )
        every {
            restTemplate.exchange("$url/query-service/players?leagueId=$leagueId",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(players))
        val query = GetPlayersQuery(leagueId)
        // when
        val result = handler.handle(query)
        // then
        result.forEachIndexed { index, player ->
            assertEquals(players[index].id, player.id)
            assertEquals(players[index].name, player.name)
            assertEquals(players[index].deviation, player.deviation)
            assertEquals(players[index].rating, player.rating)
        }
    }

    @Test
    internal fun `Should return empty list for players by league id when response is empty`() {
        // given
        val leagueId = UUID.randomUUID()
        every {
            restTemplate.exchange("$url/query-service/players?leagueId=$leagueId",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.empty())
        val query = GetPlayersQuery(leagueId)
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.isEmpty())
    }
}