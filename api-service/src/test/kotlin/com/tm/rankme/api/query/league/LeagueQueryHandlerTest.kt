package com.tm.rankme.api.query.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.api.query.Item
import com.tm.rankme.api.query.Page
import com.tm.rankme.api.query.QueryException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.*
import kotlin.random.Random
import kotlin.test.*

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

    @Test
    internal fun `Should return leagues connection`() {
        // given
        val leagues = List(5) {
            League(randomNanoId(), "League-${Random.nextInt()}", Random.nextBoolean(), Random.nextInt(10))
        }
        val page = Page(leagues.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/leagues?first=5",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetLeaguesQuery(5)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(leagues.first().id, result.pageInfo.startCursor.value)
        assertEquals(leagues.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            assertEquals(leagues[index].id, edge.cursor.value)
            assertEquals(leagues[index].id, edge.node.id)
            assertEquals(leagues[index].name, edge.node.name)
            assertEquals(leagues[index].allowDraws, edge.node.allowDraws)
            assertEquals(leagues[index].maxScore, edge.node.maxScore)
        }
    }

    @Test
    internal fun `Should return leagues connection for given cursor after`() {
        // given
        val leagues = List(7) {
            League(randomNanoId(), "League-${Random.nextInt()}", Random.nextBoolean(), Random.nextInt(10))
        }
        val page = Page(leagues.map { Item(it, it.id) }, true, false)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/leagues?first=7&after=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetLeaguesQuery(7, after = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(leagues.first().id, result.pageInfo.startCursor.value)
        assertEquals(leagues.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            assertEquals(leagues[index].id, edge.cursor.value)
            assertEquals(leagues[index].id, edge.node.id)
            assertEquals(leagues[index].name, edge.node.name)
            assertEquals(leagues[index].allowDraws, edge.node.allowDraws)
            assertEquals(leagues[index].maxScore, edge.node.maxScore)
        }
    }

    @Test
    internal fun `Should return leagues connection for given cursor before`() {
        // given
        val leagues = List(4) {
            League(randomNanoId(), "League-${Random.nextInt()}", Random.nextBoolean(), Random.nextInt(10))
        }
        val page = Page(leagues.map { Item(it, it.id) }, false, true)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/leagues?first=4&before=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetLeaguesQuery(4, before = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(leagues.first().id, result.pageInfo.startCursor.value)
        assertEquals(leagues.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            assertEquals(leagues[index].id, edge.cursor.value)
            assertEquals(leagues[index].id, edge.node.id)
            assertEquals(leagues[index].name, edge.node.name)
            assertEquals(leagues[index].allowDraws, edge.node.allowDraws)
            assertEquals(leagues[index].maxScore, edge.node.maxScore)
        }
    }

    @Test
    internal fun `Should return empty leagues connection`() {
        // given
        val page = Page(emptyList<Item<League>>(), false, false)
        every {
            restTemplate.exchange("$url/query-service/leagues?first=3",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetLeaguesQuery(3)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertNull(result.pageInfo.startCursor)
        assertNull(result.pageInfo.endCursor)
        assertTrue(result.edges.isEmpty())
    }

    @Test
    internal fun `Should throw exception when leagues connection response body is empty`() {
        // given
        val endpoint = "$url/query-service/leagues?first=3"
        every {
            restTemplate.exchange(endpoint, HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.ok().body(null)
        val query = GetLeaguesQuery(3)
        // when
        val exception = assertFailsWith<QueryException> { handler.handle(query) }
        // then
        assertEquals("Empty response body for GET query=$endpoint", exception.message)
    }
}