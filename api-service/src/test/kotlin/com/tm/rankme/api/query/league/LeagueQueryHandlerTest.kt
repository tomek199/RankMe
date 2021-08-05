package com.tm.rankme.api.query.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class LeagueQueryHandlerTest {
    private val restTemplate = mockk<RestTemplate>()
    private val url = "http://gateway"
    private val handler = LeagueQueryHandler(restTemplate, url)

    @Test
    internal fun `Should return league`() {
        // given
        val league = League(randomNanoId(), "Start Wars", false, 3)
        every { restTemplate.getForObject("$url/query-service/leagues/${league.id}", League::class.java) } returns league
        val query = GetLeagueQuery(league.id)
        // when
        val result = handler.handle(query)
        // then
        assertNotNull(result)
        assertEquals(league.id, result.id)
        assertEquals(league.name, result.name)
        assertEquals(league.allowDraws, result.allowDraws)
        assertEquals(league.maxScore, result.maxScore)
    }

    @Test
    internal fun `Should return null for league`() {
        // given
        val query = GetLeagueQuery(randomNanoId())
        every { restTemplate.getForObject("$url/query-service/leagues/${query.id}", League::class.java) } returns null
        // when
        val result = handler.handle(query)
        // then
        assertNull(result)
    }
}