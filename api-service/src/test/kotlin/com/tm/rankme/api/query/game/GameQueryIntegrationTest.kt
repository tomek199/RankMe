package com.tm.rankme.api.query.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.api.query.Item
import com.tm.rankme.api.query.Page
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
    internal fun `Should return games`() {
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
            restTemplate.exchange("$url/query-service/leagues/$leagueId/games?first=5&after=MTYyMjg0MDE1NzI0Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/games.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.games.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.games.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.games.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.games.pageInfo.endCursor"))

        games.forEachIndexed {index, game ->
            assertEquals(game.id, response.get("$.data.games.edges[$index].cursor"))
            assertEquals(game.id, response.get("$.data.games.edges[$index].node.id"))
            assertEquals(game.dateTime.toString(), response.get("$.data.games.edges[$index].node.dateTime"))
            assertEquals(game.playerOneId, response.get("$.data.games.edges[$index].node.playerOneId"))
            assertEquals(game.playerOneName, response.get("$.data.games.edges[$index].node.playerOneName"))
            assertEquals(game.playerOneDeviation, response.get("$.data.games.edges[$index].node.playerOneDeviation", Int::class.java))
            assertEquals(game.playerOneRating, response.get("$.data.games.edges[$index].node.playerOneRating", Int::class.java))
            assertEquals(game.playerTwoId, response.get("$.data.games.edges[$index].node.playerTwoId"))
            assertEquals(game.playerTwoName, response.get("$.data.games.edges[$index].node.playerTwoName"))
            assertEquals(game.playerTwoDeviation, response.get("$.data.games.edges[$index].node.playerTwoDeviation", Int::class.java))
            assertEquals(game.playerTwoRating, response.get("$.data.games.edges[$index].node.playerTwoRating", Int::class.java))
            if (game is CompletedGame) {
                assertEquals(game.result.playerOneScore, response.get("$.data.games.edges[$index].node.result.playerOneScore", Int::class.java))
                assertEquals(game.result.playerOneDeviationDelta, response.get("$.data.games.edges[$index].node.result.playerOneDeviationDelta", Int::class.java))
                assertEquals(game.result.playerOneRatingDelta, response.get("$.data.games.edges[$index].node.result.playerOneRatingDelta", Int::class.java))
                assertEquals(game.result.playerTwoScore, response.get("$.data.games.edges[$index].node.result.playerTwoScore", Int::class.java))
                assertEquals(game.result.playerTwoDeviationDelta, response.get("$.data.games.edges[$index].node.result.playerTwoDeviationDelta", Int::class.java))
                assertEquals(game.result.playerTwoRatingDelta, response.get("$.data.games.edges[$index].node.result.playerTwoRatingDelta", Int::class.java))
            }
        }
    }
}