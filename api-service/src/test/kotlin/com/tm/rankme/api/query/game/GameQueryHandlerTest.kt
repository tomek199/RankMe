package com.tm.rankme.api.query.game

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
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random
import kotlin.test.*

internal class GameQueryHandlerTest {
    private val restTemplate = mockk<RestTemplate>()
    private val url = "http://gateway"
    private val handler = GameQueryHandler(restTemplate, url)

    @Test
    internal fun `Should return league games connection`() {
        // given
        val leagueId = UUID.randomUUID()
        val games = List(6) {
            Game(
                UUID.randomUUID(), LocalDateTime.now(),
                UUID.randomUUID(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(games.map { Item(it, it.id.toString()) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/games?first=6",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForLeagueQuery(leagueId, 6)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(games[0].id.toString(), result.pageInfo.startCursor.value)
        assertEquals(games[5].id.toString(), result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            assertEquals(games[index].id.toString(), edge.cursor.value)
            assertEquals(games[index].id, edge.node.id)
            assertEquals(games[index].dateTime, edge.node.dateTime)
            assertEquals(games[index].playerOneId, edge.node.playerOneId)
            assertEquals(games[index].playerOneName, edge.node.playerOneName)
            assertEquals(games[index].playerOneDeviation, edge.node.playerOneDeviation)
            assertEquals(games[index].playerOneRating, edge.node.playerOneRating)
            assertEquals(games[index].playerTwoId, edge.node.playerTwoId)
            assertEquals(games[index].playerTwoName, edge.node.playerTwoName)
            assertEquals(games[index].playerTwoDeviation, edge.node.playerTwoDeviation)
            assertEquals(games[index].playerTwoRating, edge.node.playerTwoRating)
            assertEquals(games[index].result!!.playerOneScore, edge.node.result!!.playerOneScore)
            assertEquals(games[index].result!!.playerOneDeviationDelta, edge.node.result!!.playerOneDeviationDelta)
            assertEquals(games[index].result!!.playerOneRatingDelta, edge.node.result!!.playerOneRatingDelta)
            assertEquals(games[index].result!!.playerTwoScore, edge.node.result!!.playerTwoScore)
            assertEquals(games[index].result!!.playerTwoDeviationDelta, edge.node.result!!.playerTwoDeviationDelta)
            assertEquals(games[index].result!!.playerTwoRatingDelta, edge.node.result!!.playerTwoRatingDelta)
        }
    }

    @Test
    internal fun `Should return league games connection for given cursor`() {
        // given
        val leagueId = UUID.randomUUID()
        val games = List(7) {
            Game(UUID.randomUUID(), LocalDateTime.now(),
                UUID.randomUUID(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt())
        }
        val page = Page(games.map { Item(it, it.id.toString()) }, true, false)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/games?first=7&after=${games[0].id}",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForLeagueQuery(leagueId, 7, games[0].id.toString())
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(games[0].id.toString(), result.pageInfo.startCursor.value)
        assertEquals(games[6].id.toString(), result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            assertEquals(games[index].id.toString(), edge.cursor.value)
            assertEquals(games[index].id, edge.node.id)
            assertEquals(games[index].dateTime, edge.node.dateTime)
            assertEquals(games[index].playerOneId, edge.node.playerOneId)
            assertEquals(games[index].playerOneName, edge.node.playerOneName)
            assertEquals(games[index].playerOneDeviation, edge.node.playerOneDeviation)
            assertEquals(games[index].playerOneRating, edge.node.playerOneRating)
            assertEquals(games[index].playerTwoId, edge.node.playerTwoId)
            assertEquals(games[index].playerTwoName, edge.node.playerTwoName)
            assertEquals(games[index].playerTwoDeviation, edge.node.playerTwoDeviation)
            assertEquals(games[index].playerTwoRating, edge.node.playerTwoRating)
            assertNull(edge.node.result)
        }
    }

    @Test
    internal fun `Should throw exception when league games connection response body is empty`() {
        // given
        val leagueId = UUID.randomUUID()
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/games?first=12",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.ok().body(null)
        val query = GetGamesForLeagueQuery(leagueId, 12)
        // when
        val exception = assertFailsWith<QueryException> { handler.handle(query) }
        // then
        assertEquals(
            "Empty response body for query=GetGamesForLeagueQuery(leagueId=$leagueId, first=12, after=null)",
            exception.message
        )
    }

    @Test
    internal fun `Should return player games connection`() {
        // given
        val playerId = UUID.randomUUID()
        val games = List(4) {
            Game(
                UUID.randomUUID(), LocalDateTime.now(),
                UUID.randomUUID(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(games.map { Item(it, it.id.toString()) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/games?first=4",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForPlayerQuery(playerId, 4)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(games[0].id.toString(), result.pageInfo.startCursor.value)
        assertEquals(games[3].id.toString(), result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            assertEquals(games[index].id.toString(), edge.cursor.value)
            assertEquals(games[index].id, edge.node.id)
            assertEquals(games[index].dateTime, edge.node.dateTime)
            assertEquals(games[index].playerOneId, edge.node.playerOneId)
            assertEquals(games[index].playerOneName, edge.node.playerOneName)
            assertEquals(games[index].playerOneDeviation, edge.node.playerOneDeviation)
            assertEquals(games[index].playerOneRating, edge.node.playerOneRating)
            assertEquals(games[index].playerTwoId, edge.node.playerTwoId)
            assertEquals(games[index].playerTwoName, edge.node.playerTwoName)
            assertEquals(games[index].playerTwoDeviation, edge.node.playerTwoDeviation)
            assertEquals(games[index].playerTwoRating, edge.node.playerTwoRating)
            assertEquals(games[index].result!!.playerOneScore, edge.node.result!!.playerOneScore)
            assertEquals(games[index].result!!.playerOneDeviationDelta, edge.node.result!!.playerOneDeviationDelta)
            assertEquals(games[index].result!!.playerOneRatingDelta, edge.node.result!!.playerOneRatingDelta)
            assertEquals(games[index].result!!.playerTwoScore, edge.node.result!!.playerTwoScore)
            assertEquals(games[index].result!!.playerTwoDeviationDelta, edge.node.result!!.playerTwoDeviationDelta)
            assertEquals(games[index].result!!.playerTwoRatingDelta, edge.node.result!!.playerTwoRatingDelta)
        }
    }

    @Test
    internal fun `Should return player games connection for given cursor`() {
        // given
        val playerId = UUID.randomUUID()
        val games = List(5) {
            Game(UUID.randomUUID(), LocalDateTime.now(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt())
        }
        val page = Page(games.map { Item(it, it.id.toString()) }, true, false)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/games?first=5&after=${games[0].id}",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForPlayerQuery(playerId, 5, games[0].id.toString())
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(games[0].id.toString(), result.pageInfo.startCursor.value)
        assertEquals(games[4].id.toString(), result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            assertEquals(games[index].id.toString(), edge.cursor.value)
            assertEquals(games[index].id, edge.node.id)
            assertEquals(games[index].dateTime, edge.node.dateTime)
            assertEquals(games[index].playerOneId, edge.node.playerOneId)
            assertEquals(games[index].playerOneName, edge.node.playerOneName)
            assertEquals(games[index].playerOneDeviation, edge.node.playerOneDeviation)
            assertEquals(games[index].playerOneRating, edge.node.playerOneRating)
            assertEquals(games[index].playerTwoId, edge.node.playerTwoId)
            assertEquals(games[index].playerTwoName, edge.node.playerTwoName)
            assertEquals(games[index].playerTwoDeviation, edge.node.playerTwoDeviation)
            assertEquals(games[index].playerTwoRating, edge.node.playerTwoRating)
            assertNull(edge.node.result)
        }
    }

    @Test
    internal fun `Should throw exception when player games connection response body is empty`() {
        // given
        val playerId = UUID.randomUUID()
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/games?first=11",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.ok().body(null)
        val query = GetGamesForPlayerQuery(playerId, 11)
        // when
        val exception = assertFailsWith<QueryException> { handler.handle(query) }
        // then
        assertEquals(
            "Empty response body for query=GetGamesForPlayerQuery(playerId=$playerId, first=11, after=null)",
            exception.message
        )
    }
}