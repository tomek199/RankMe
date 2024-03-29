package com.tm.rankme.query.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.query.Item
import com.tm.rankme.query.Page
import com.tm.rankme.query.assertCompletedGame
import com.tm.rankme.query.assertScheduledGame
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test"])
internal class GameQueryIntegrationTest {
    @Autowired
    private lateinit var template: GraphQLTestTemplate
    @Value("\${gateway.url}")
    private lateinit var url: String
    @MockkBean
    private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `Should return games after given cursor`() {
        // given
        val leagueId = "HKQqvVmIT-MD3XwgUFK9-"
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
        val scheduledGames = List(2) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, true, false)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/games?first=5&after=MTYyMjg0MDE1NzI0Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/games-after.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.games.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.games.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.games.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.games.pageInfo.endCursor"))

        games.forEachIndexed {index, game ->
            if (game is CompletedGame) assertCompletedGame(game, response, "$.data.games.edges[$index]")
            else if (game is ScheduledGame) assertScheduledGame(game, response, "$.data.games.edges[$index]")
        }
    }

    @Test
    internal fun `Should return games before given cursor`() {
        // given
        val leagueId = "HKQqvVmIT-MD3XwgUFK9-"
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
            restTemplate.exchange("$url/query-service/leagues/$leagueId/games?first=5&before=MTYyMjg0MDE1NzI0Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/games-before.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.games.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.games.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.games.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.games.pageInfo.endCursor"))

        games.forEachIndexed {index, game ->
            if (game is CompletedGame) assertCompletedGame(game, response, "$.data.games.edges[$index]")
            else if (game is ScheduledGame) assertScheduledGame(game, response, "$.data.games.edges[$index]")
        }
    }

    @Test
    internal fun `Should return completed games after given cursor`() {
        // given
        val leagueId = "HKQqvVmIT-MD3XwgUFK9-"
        val completedGames = List(5) {
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
        val page = Page(completedGames.map { Item(it, it.id) }, true, false)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/completed-games?first=5&after=MTYyMjg0MDE1NzI0Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/completed-games-after.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.completedGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.completedGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.completedGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.completedGames.pageInfo.endCursor"))

        completedGames.forEachIndexed {index, game ->
            assertCompletedGame(game, response, "$.data.completedGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return completed games before given cursor`() {
        // given
        val leagueId = "HKQqvVmIT-MD3XwgUFK9-"
        val completedGames = List(5) {
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
            restTemplate.exchange("$url/query-service/leagues/$leagueId/completed-games?first=5&before=MTYyMjg0MDE1NzI0Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/completed-games-before.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.completedGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.completedGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.completedGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.completedGames.pageInfo.endCursor"))

        completedGames.forEachIndexed {index, game ->
            assertCompletedGame(game, response, "$.data.completedGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return scheduled games after given cursor`() {
        // given
        val leagueId = "HKQqvVmIT-MD3XwgUFK9-"
        val scheduledGames = List(5) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, true, false)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/scheduled-games?first=5&after=MTYyMjg0MDE1NzI0Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/scheduled-games-after.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.scheduledGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.scheduledGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.scheduledGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.scheduledGames.pageInfo.endCursor"))

        scheduledGames.forEachIndexed {index, game ->
            assertScheduledGame(game, response, "$.data.scheduledGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return scheduled games before given cursor`() {
        // given
        val leagueId = "HKQqvVmIT-MD3XwgUFK9-"
        val scheduledGames = List(5) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/leagues/$leagueId/scheduled-games?first=5&before=MTYyMjg0MDE1NzI0Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/scheduled-games-before.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.scheduledGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.scheduledGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.scheduledGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.scheduledGames.pageInfo.endCursor"))

        scheduledGames.forEachIndexed {index, game ->
            assertScheduledGame(game, response, "$.data.scheduledGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return player games after given cursor`() {
        // given
        val playerId = "UmLdL65Hl_1_FqcsSUhDq"
        val completedGames = List(3) {
            CompletedGame(
                randomNanoId(), LocalDateTime.now(),
                playerId, "Player-$playerId", Random.nextInt(), Random.nextInt(),
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
                playerId, "Player-$playerId", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, true, false)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/games?first=5&after=MTY1MDA5NzM5NTE5MQ==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/player-games-after.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.playerGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.playerGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.playerGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.playerGames.pageInfo.endCursor"))

        games.forEachIndexed {index, game ->
            if (game is CompletedGame) assertCompletedGame(game, response, "$.data.playerGames.edges[$index]")
            else if (game is ScheduledGame) assertScheduledGame(game, response, "$.data.playerGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return player games before given cursor`() {
        // given
        val playerId = "UmLdL65Hl_1_FqcsSUhDq"
        val completedGames = List(3) {
            CompletedGame(
                randomNanoId(), LocalDateTime.now(),
                playerId, "Player-$playerId", Random.nextInt(), Random.nextInt(),
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
                playerId, "Player-$playerId", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/games?first=5&before=MTY1MDA5NzM5NTE5MQ==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/player-games-before.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.playerGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.playerGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.playerGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.playerGames.pageInfo.endCursor"))

        games.forEachIndexed {index, game ->
            if (game is CompletedGame) assertCompletedGame(game, response, "$.data.playerGames.edges[$index]")
            else if (game is ScheduledGame) assertScheduledGame(game, response, "$.data.playerGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return player completed games after given cursor`() {
        // given
        val playerId = "J4nxK4DLbBDwuoNaQng-m"
        val completedGames = List(5) {
            CompletedGame(
                randomNanoId(), LocalDateTime.now(),
                playerId, "Player-$playerId", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(completedGames.map { Item(it, it.id) }, true, false)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/completed-games?first=5&after=MTY1MDA5NzM5NTI5Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/player-completed-games-after.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.playerCompletedGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.playerCompletedGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.playerCompletedGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.playerCompletedGames.pageInfo.endCursor"))

        completedGames.forEachIndexed {index, game ->
            assertCompletedGame(game, response, "$.data.playerCompletedGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return player completed games before given cursor`() {
        // given
        val playerId = "J4nxK4DLbBDwuoNaQng-m"
        val completedGames = List(5) {
            CompletedGame(
                randomNanoId(), LocalDateTime.now(),
                playerId, "Player-$playerId", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(completedGames.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/completed-games?first=5&before=MTY1MDA5NzM5NTI5Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/player-completed-games-before.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.playerCompletedGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.playerCompletedGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.playerCompletedGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.playerCompletedGames.pageInfo.endCursor"))

        completedGames.forEachIndexed {index, game ->
            assertCompletedGame(game, response, "$.data.playerCompletedGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return player scheduled games after given cursor`() {
        // given
        val playerId = "vIgDhNhvjked02bUyKtyi"
        val scheduledGames = List(5) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                playerId, "Player-$playerId", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, true, false)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/scheduled-games?first=5&after=MTY1MDA5NzM5NTQwMg==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/player-scheduled-games-after.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.playerScheduledGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.playerScheduledGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.playerScheduledGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.playerScheduledGames.pageInfo.endCursor"))

        scheduledGames.forEachIndexed {index, game ->
            assertScheduledGame(game, response, "$.data.playerScheduledGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return player scheduled games before given cursor`() {
        // given
        val playerId = "vIgDhNhvjked02bUyKtyi"
        val scheduledGames = List(5) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                playerId, "Player-$playerId", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val page = Page(scheduledGames.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.exchange("$url/query-service/players/$playerId/scheduled-games?first=5&before=MTY1MDA5NzM5NTQwMg==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/query/player-scheduled-games-before.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.playerScheduledGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.playerScheduledGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.playerScheduledGames.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.playerScheduledGames.pageInfo.endCursor"))

        scheduledGames.forEachIndexed {index, game ->
            assertScheduledGame(game, response, "$.data.playerScheduledGames.edges[$index]")
        }
    }
}