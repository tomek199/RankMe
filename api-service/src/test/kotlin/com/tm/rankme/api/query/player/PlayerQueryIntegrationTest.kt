package com.tm.rankme.api.query.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.api.query.Item
import com.tm.rankme.api.query.Page
import com.tm.rankme.api.query.assertCompletedGame
import com.tm.rankme.api.query.assertScheduledGame
import com.tm.rankme.api.query.game.CompletedGame
import com.tm.rankme.api.query.game.Game
import com.tm.rankme.api.query.game.Result
import com.tm.rankme.api.query.game.ScheduledGame
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
        val player = Player("9KOnSx8yFXd382KJbeaqO", "Optimus Prime", 186, 2481)
        val completedGames = List(3) {
            CompletedGame(
                randomNanoId(), LocalDateTime.now(),
                player.id, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
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
        val gamesPage = Page(games.map { Item(it, it.id) }, false, true)
        val completedGamesPage = Page(completedGames.map { Item(it, it.id) }, false, true)
        val scheduledGamesPage = Page(scheduledGames.map { Item(it, it.id) }, false, true)
        every { restTemplate.getForObject("$url/query-service/players/${player.id}", Player::class.java) } returns player
        every {
            restTemplate.exchange("$url/query-service/players/${player.id}/games?first=5",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(gamesPage))
        every {
            restTemplate.exchange("$url/query-service/players/${player.id}/completed-games?first=3",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(completedGamesPage))
        every {
            restTemplate.exchange("$url/query-service/players/${player.id}/scheduled-games?first=2",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(scheduledGamesPage))

        val request = "graphql/query/player.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(player.id, response.get("$.data.player.id"))
        assertEquals(player.name, response.get("$.data.player.name"))
        assertEquals(player.deviation, response.get("$.data.player.deviation", Int::class.java))
        assertEquals(player.rating, response.get("$.data.player.rating", Int::class.java))

        assertEquals(gamesPage.hasPreviousPage, response.get("$.data.player.games.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(gamesPage.hasNextPage, response.get("$.data.player.games.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(gamesPage.items.first().cursor, response.get("$.data.player.games.pageInfo.startCursor"))
        assertEquals(gamesPage.items.last().cursor, response.get("$.data.player.games.pageInfo.endCursor"))
        games.forEachIndexed {index, game ->
            if (game is CompletedGame) assertCompletedGame(game, response, "$.data.player.games.edges[$index]")
            else if (game is ScheduledGame) assertScheduledGame(game, response, "$.data.player.games.edges[$index]")
        }

        assertEquals(completedGamesPage.hasPreviousPage, response.get("$.data.player.completedGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(completedGamesPage.hasNextPage, response.get("$.data.player.completedGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(completedGamesPage.items.first().cursor, response.get("$.data.player.completedGames.pageInfo.startCursor"))
        assertEquals(completedGamesPage.items.last().cursor, response.get("$.data.player.completedGames.pageInfo.endCursor"))
        completedGames.forEachIndexed { index, game ->
            assertCompletedGame(game, response, "$.data.player.completedGames.edges[$index]")
        }

        assertEquals(scheduledGamesPage.hasPreviousPage, response.get("$.data.player.scheduledGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(scheduledGamesPage.hasNextPage, response.get("$.data.player.scheduledGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(scheduledGamesPage.items.first().cursor, response.get("$.data.player.scheduledGames.pageInfo.startCursor"))
        assertEquals(scheduledGamesPage.items.last().cursor, response.get("$.data.player.scheduledGames.pageInfo.endCursor"))
        scheduledGames.forEachIndexed { index, game ->
            assertScheduledGame(game, response, "$.data.player.scheduledGames.edges[$index]")
        }
    }

    @Test
    internal fun `Should return players`() {
        // given
        val leagueId = "xAeksxNOS-lq5mnKmm1tk"
        val players = listOf(
            Player(randomNanoId(), "Optimus Prime", 145, 2746),
            Player(randomNanoId(), "Bumblebee", 213, 1863),
            Player(randomNanoId(), "Megatron", 327, 2175),
        )
        every {
            restTemplate.exchange("$url/query-service/leagues/${leagueId}/players",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(players))
        val request = "graphql/query/players.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        players.forEachIndexed {index, player ->
            assertEquals(player.id, response.get("$.data.players[$index].id"))
            assertEquals(player.name, response.get("$.data.players[$index].name"))
            assertEquals(player.deviation, response.get("$.data.players[$index].deviation", Int::class.java))
            assertEquals(player.rating, response.get("$.data.players[$index].rating", Int::class.java))
        }
    }
}