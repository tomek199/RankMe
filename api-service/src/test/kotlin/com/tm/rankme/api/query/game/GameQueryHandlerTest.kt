package com.tm.rankme.api.query.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.api.query.Item
import com.tm.rankme.api.query.Page
import com.tm.rankme.api.query.QueryException
import graphql.relay.Edge
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
        val leagueId = randomNanoId()
        val completedGames = List(3) {
            CompletedGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val scheduledGames = List(3) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, false, true)
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
        assertEquals(games.first().id, result.pageInfo.startCursor.value)
        assertEquals(games.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            games[index].let { expectedGame ->
                if (expectedGame is CompletedGame) assertCompletedGame(expectedGame, edge as Edge<CompletedGame>)
                else if (expectedGame is ScheduledGame) assertScheduledGame(expectedGame, edge as Edge<ScheduledGame>)
            }
        }
    }

    @Test
    internal fun `Should return league games connection for given cursor after`() {
        // given
        val leagueId = randomNanoId()
        val completedGames = List(4) {
            CompletedGame(randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val scheduledGames = List(3) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, true, false)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/games?first=7&after=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForLeagueQuery(leagueId, 7, after = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(games.first().id, result.pageInfo.startCursor.value)
        assertEquals(games.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            games[index].let { expectedGame ->
                if (expectedGame is CompletedGame) assertCompletedGame(expectedGame, edge as Edge<CompletedGame>)
                else if (expectedGame is ScheduledGame) assertScheduledGame(expectedGame, edge as Edge<ScheduledGame>)
            }
        }
    }

    @Test
    internal fun `Should return league games connection for given cursor before`() {
        // given
        val leagueId = randomNanoId()
        val completedGames = List(6) {
            CompletedGame(randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val scheduledGames = List(2) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, false, true)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/games?first=8&before=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForLeagueQuery(leagueId, 8, before = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(games.first().id, result.pageInfo.startCursor.value)
        assertEquals(games.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            games[index].let { expectedGame ->
                if (expectedGame is CompletedGame) assertCompletedGame(expectedGame, edge as Edge<CompletedGame>)
                else if (expectedGame is ScheduledGame) assertScheduledGame(expectedGame, edge as Edge<ScheduledGame>)
            }
        }
    }

    @Test
    internal fun `Should return empty league games connection`() {
        // given
        val leagueId = randomNanoId()
        val page = Page(emptyList<Item<Game>>(), false, false)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/games?first=4",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForLeagueQuery(leagueId, 4)
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
    internal fun `Should throw exception when league games connection response body is empty`() {
        // given
        val leagueId = randomNanoId()
        val endpoint = "$url/query-service/leagues/$leagueId/games?first=12"
        every {
            restTemplate.exchange(endpoint, HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.ok().body(null)
        val query = GetGamesForLeagueQuery(leagueId, 12)
        // when
        val exception = assertFailsWith<QueryException> { handler.handle(query) }
        // then
        assertEquals("Empty response body for GET query=$endpoint", exception.message)
    }

    @Test
    internal fun `Should return league completed games connection`() {
        // given
        val leagueId = randomNanoId()
        val completedGames = List(3) {
            CompletedGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(completedGames.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/completed-games?first=6",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetCompletedGamesForLeagueQuery(leagueId, 6)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(completedGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(completedGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertCompletedGame(completedGames[index], edge) }
    }

    @Test
    internal fun `Should return league completed games connection for given cursor after`() {
        // given
        val leagueId = randomNanoId()
        val completedGames = List(4) {
            CompletedGame(randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(completedGames.map { Item(it, it.id) }, true, false)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/completed-games?first=7&after=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetCompletedGamesForLeagueQuery(leagueId, 7, after = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(completedGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(completedGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertCompletedGame(completedGames[index], edge) }
    }

    @Test
    internal fun `Should return league completed games connection for given cursor before`() {
        // given
        val leagueId = randomNanoId()
        val completedGames = List(7) {
            CompletedGame(randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(completedGames.map { Item(it, it.id) }, false, true)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/completed-games?first=7&before=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetCompletedGamesForLeagueQuery(leagueId, 7, before = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(completedGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(completedGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertCompletedGame(completedGames[index], edge) }
    }

    @Test
    internal fun `Should return empty league completed games connection`() {
        // given
        val leagueId = randomNanoId()
        val page = Page(emptyList<Item<Game>>(), false, false)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/completed-games?first=4",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetCompletedGamesForLeagueQuery(leagueId, 4)
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
    internal fun `Should throw exception when league completed games connection response body is empty`() {
        // given
        val leagueId = randomNanoId()
        val endpoint = "$url/query-service/leagues/$leagueId/completed-games?first=12"
        every {
            restTemplate.exchange(endpoint, HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.ok().body(null)
        val query = GetCompletedGamesForLeagueQuery(leagueId, 12)
        // when
        val exception = assertFailsWith<QueryException> { handler.handle(query) }
        // then
        assertEquals("Empty response body for GET query=$endpoint", exception.message)
    }

    @Test
    internal fun `Should return league scheduled games connection`() {
        // given
        val leagueId = randomNanoId()
        val scheduledGames = List(3) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/scheduled-games?first=6",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetScheduledGamesForLeagueQuery(leagueId, 6)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(scheduledGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(scheduledGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertScheduledGame(scheduledGames[index], edge) }
    }

    @Test
    internal fun `Should return league scheduled games connection for given cursor after`() {
        // given
        val leagueId = randomNanoId()
        val scheduledGames = List(3) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, true, false)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/scheduled-games?first=7&after=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetScheduledGamesForLeagueQuery(leagueId, 7, after = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(scheduledGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(scheduledGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertScheduledGame(scheduledGames[index], edge) }
    }

    @Test
    internal fun `Should return league scheduled games connection for given cursor before`() {
        // given
        val leagueId = randomNanoId()
        val scheduledGames = List(5) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, false, true)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/scheduled-games?first=5&before=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetScheduledGamesForLeagueQuery(leagueId, 5, before = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(scheduledGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(scheduledGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertScheduledGame(scheduledGames[index], edge) }
    }

    @Test
    internal fun `Should return empty league scheduled games connection`() {
        // given
        val leagueId = randomNanoId()
        val page = Page(emptyList<Item<Game>>(), false, false)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/scheduled-games?first=4",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetScheduledGamesForLeagueQuery(leagueId, 4)
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
    internal fun `Should throw exception when league scheduled games connection response body is empty`() {
        // given
        val leagueId = randomNanoId()
        val endpoint = "$url/query-service/leagues/$leagueId/scheduled-games?first=12"
        every {
            restTemplate.exchange(endpoint, HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.ok().body(null)
        val query = GetScheduledGamesForLeagueQuery(leagueId, 12)
        // when
        val exception = assertFailsWith<QueryException> { handler.handle(query) }
        // then
        assertEquals("Empty response body for GET query=$endpoint", exception.message)
    }

    @Test
    internal fun `Should return player games connection`() {
        // given
        val playerId = randomNanoId()
        val completedGames = List(2) {
            CompletedGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val scheduledGames = List(2) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, false, true)
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
        assertEquals(games.first().id, result.pageInfo.startCursor.value)
        assertEquals(games.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            games[index].let { expectedGame ->
                if (expectedGame is CompletedGame) assertCompletedGame(expectedGame, edge as Edge<CompletedGame>)
                else if (expectedGame is ScheduledGame) assertScheduledGame(expectedGame, edge as Edge<ScheduledGame>)
            }
        }
    }

    @Test
    internal fun `Should return player games connection for given cursor after`() {
        // given
        val playerId = randomNanoId()
        val completedGames = List(2) {
            CompletedGame(randomNanoId(), LocalDateTime.now(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val scheduledGames = List(3) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, true, false)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/games?first=5&after=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForPlayerQuery(playerId, 5, after = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(games.first().id, result.pageInfo.startCursor.value)
        assertEquals(games.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            games[index].let { expectedGame ->
                if (expectedGame is CompletedGame) assertCompletedGame(expectedGame, edge as Edge<CompletedGame>)
                else if (expectedGame is ScheduledGame) assertScheduledGame(expectedGame, edge as Edge<ScheduledGame>)
            }
        }
    }

    @Test
    internal fun `Should return player games connection for given cursor before`() {
        // given
        val playerId = randomNanoId()
        val completedGames = List(2) {
            CompletedGame(randomNanoId(), LocalDateTime.now(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val scheduledGames = List(3) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, false, true)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/games?first=5&before=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForPlayerQuery(playerId, 5, before = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(games.first().id, result.pageInfo.startCursor.value)
        assertEquals(games.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge ->
            games[index].let { expectedGame ->
                if (expectedGame is CompletedGame) assertCompletedGame(expectedGame, edge as Edge<CompletedGame>)
                else if (expectedGame is ScheduledGame) assertScheduledGame(expectedGame, edge as Edge<ScheduledGame>)
            }
        }
    }

    @Test
    internal fun `Should return empty player games connection`() {
        // given
        val playerId = randomNanoId()
        val page = Page(emptyList<Item<Game>>(), false, false)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/games?first=6",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetGamesForPlayerQuery(playerId, 6)
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
    internal fun `Should throw exception when player games connection response body is empty`() {
        // given
        val playerId = randomNanoId()
        val endpoint = "$url/query-service/players/$playerId/games?first=11"
        every {
            restTemplate.exchange(endpoint, HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.ok().body(null)
        val query = GetGamesForPlayerQuery(playerId, 11)
        // when
        val exception = assertFailsWith<QueryException> { handler.handle(query) }
        // then
        assertEquals("Empty response body for GET query=$endpoint", exception.message)
    }

    @Test
    internal fun `Should return player completed games connection`() {
        // given
        val playerId = randomNanoId()
        val completedGames = List(2) {
            CompletedGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(completedGames.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/completed-games?first=4",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetCompletedGamesForPlayerQuery(playerId, 4)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(completedGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(completedGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertCompletedGame(completedGames[index], edge) }
    }

    @Test
    internal fun `Should return player completed games connection for given cursor after`() {
        // given
        val playerId = randomNanoId()
        val completedGames = List(2) {
            CompletedGame(randomNanoId(), LocalDateTime.now(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(completedGames.map { Item(it, it.id) }, true, false)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/completed-games?first=5&after=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetCompletedGamesForPlayerQuery(playerId, 5, after = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(completedGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(completedGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertCompletedGame(completedGames[index], edge) }
    }

    @Test
    internal fun `Should return player completed games connection for given cursor before`() {
        // given
        val playerId = randomNanoId()
        val completedGames = List(4) {
            CompletedGame(randomNanoId(), LocalDateTime.now(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(completedGames.map { Item(it, it.id) }, false, true)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/completed-games?first=4&before=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetCompletedGamesForPlayerQuery(playerId, 4, before = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(completedGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(completedGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertCompletedGame(completedGames[index], edge) }
    }

    @Test
    internal fun `Should return empty player completed games connection`() {
        // given
        val playerId = randomNanoId()
        val page = Page(emptyList<Item<Game>>(), false, false)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/completed-games?first=6",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetCompletedGamesForPlayerQuery(playerId, 6)
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
    internal fun `Should throw exception when player completed games connection response body is empty`() {
        // given
        val playerId = randomNanoId()
        val endpoint = "$url/query-service/players/$playerId/completed-games?first=11"
        every {
            restTemplate.exchange(endpoint, HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.ok().body(null)
        val query = GetCompletedGamesForPlayerQuery(playerId, 11)
        // when
        val exception = assertFailsWith<QueryException> { handler.handle(query) }
        // then
        assertEquals("Empty response body for GET query=$endpoint", exception.message)
    }

    @Test
    internal fun `Should return player scheduled games connection`() {
        // given
        val playerId = randomNanoId()
        val scheduledGames = List(2) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/scheduled-games?first=4",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetScheduledGamesForPlayerQuery(playerId, 4)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(scheduledGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(scheduledGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertScheduledGame(scheduledGames[index], edge) }
    }

    @Test
    internal fun `Should return player scheduled games connection for given cursor after`() {
        // given
        val playerId = randomNanoId()
        val scheduledGames = List(3) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, true, false)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/scheduled-games?first=5&after=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetScheduledGamesForPlayerQuery(playerId, 5, after = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertTrue(result.pageInfo.isHasPreviousPage)
        assertFalse(result.pageInfo.isHasNextPage)
        assertEquals(scheduledGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(scheduledGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertScheduledGame(scheduledGames[index], edge) }
    }

    @Test
    internal fun `Should return player scheduled games connection for given cursor before`() {
        // given
        val playerId = randomNanoId()
        val scheduledGames = List(5) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, false, true)
        val cursor = Base64.getEncoder().encodeToString(randomNanoId().toByteArray())
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/scheduled-games?first=5&before=$cursor",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetScheduledGamesForPlayerQuery(playerId, 5, before = cursor)
        // when
        val result = handler.handle(query)
        // then
        assertFalse(result.pageInfo.isHasPreviousPage)
        assertTrue(result.pageInfo.isHasNextPage)
        assertEquals(scheduledGames.first().id, result.pageInfo.startCursor.value)
        assertEquals(scheduledGames.last().id, result.pageInfo.endCursor.value)
        result.edges.forEachIndexed { index, edge -> assertScheduledGame(scheduledGames[index], edge) }
    }

    @Test
    internal fun `Should return empty player scheduled games connection`() {
        // given
        val playerId = randomNanoId()
        val page = Page(emptyList<Item<Game>>(), false, false)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/scheduled-games?first=6",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val query = GetScheduledGamesForPlayerQuery(playerId, 6)
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
    internal fun `Should throw exception when player scheduled games connection response body is empty`() {
        // given
        val playerId = randomNanoId()
        val endpoint = "$url/query-service/players/$playerId/scheduled-games?first=11"
        every {
            restTemplate.exchange(endpoint, HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.ok().body(null)
        val query = GetScheduledGamesForPlayerQuery(playerId, 11)
        // when
        val exception = assertFailsWith<QueryException> { handler.handle(query) }
        // then
        assertEquals("Empty response body for GET query=$endpoint", exception.message)
    }

    private fun assertCompletedGame(expectedGame: CompletedGame, edge: Edge<CompletedGame>) {
        assertEquals(expectedGame.id, edge.cursor.value)
        assertEquals(expectedGame.id, edge.node.id)
        assertEquals(expectedGame.dateTime, edge.node.dateTime)
        assertEquals(expectedGame.playerOneId, edge.node.playerOneId)
        assertEquals(expectedGame.playerOneName, edge.node.playerOneName)
        assertEquals(expectedGame.playerOneDeviation, edge.node.playerOneDeviation)
        assertEquals(expectedGame.playerOneRating, edge.node.playerOneRating)
        assertEquals(expectedGame.playerTwoId, edge.node.playerTwoId)
        assertEquals(expectedGame.playerTwoName, edge.node.playerTwoName)
        assertEquals(expectedGame.playerTwoDeviation, edge.node.playerTwoDeviation)
        assertEquals(expectedGame.playerTwoRating, edge.node.playerTwoRating)
        assertEquals(expectedGame.result.playerOneScore, edge.node.result.playerOneScore)
        assertEquals(expectedGame.result.playerOneDeviationDelta, edge.node.result.playerOneDeviationDelta)
        assertEquals(expectedGame.result.playerOneRatingDelta, edge.node.result.playerOneRatingDelta)
        assertEquals(expectedGame.result.playerTwoScore, edge.node.result.playerTwoScore)
        assertEquals(expectedGame.result.playerTwoDeviationDelta, edge.node.result.playerTwoDeviationDelta)
        assertEquals(expectedGame.result.playerTwoRatingDelta, edge.node.result.playerTwoRatingDelta)
    }

    private fun assertScheduledGame(expectedGame: ScheduledGame, edge: Edge<ScheduledGame>) {
        assertEquals(expectedGame.id, edge.cursor.value)
        assertEquals(expectedGame.id, edge.node.id)
        assertEquals(expectedGame.dateTime, edge.node.dateTime)
        assertEquals(expectedGame.playerOneId, edge.node.playerOneId)
        assertEquals(expectedGame.playerOneName, edge.node.playerOneName)
        assertEquals(expectedGame.playerOneDeviation, edge.node.playerOneDeviation)
        assertEquals(expectedGame.playerOneRating, edge.node.playerOneRating)
        assertEquals(expectedGame.playerTwoId, edge.node.playerTwoId)
        assertEquals(expectedGame.playerTwoName, edge.node.playerTwoName)
        assertEquals(expectedGame.playerTwoDeviation, edge.node.playerTwoDeviation)
        assertEquals(expectedGame.playerTwoRating, edge.node.playerTwoRating)
    }
}