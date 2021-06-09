package com.tm.rankme.api.query.game

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
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        val leagueId = "9212d98b-18a0-4807-9160-7ce0f99f690b"
        val games = List(5) {
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
            restTemplate.exchange("$url/query-service/games?leagueId=${leagueId}&first=5&after=MTYyMjg0MDE1NzI0Nw==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/get-games.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.getGames.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.getGames.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items[0].cursor, response.get("$.data.getGames.pageInfo.startCursor"))
        assertEquals(page.items[4].cursor, response.get("$.data.getGames.pageInfo.endCursor"))

        games.forEachIndexed {index, game ->
            assertEquals(game.id.toString(), response.get("$.data.getGames.edges[$index].cursor"))
            assertEquals(game.id.toString(), response.get("$.data.getGames.edges[$index].node.id"))
            assertEquals(game.dateTime.toString(), response.get("$.data.getGames.edges[$index].node.dateTime"))
            assertEquals(game.playerOneId.toString(), response.get("$.data.getGames.edges[$index].node.playerOneId"))
            assertEquals(game.playerOneName, response.get("$.data.getGames.edges[$index].node.playerOneName"))
            assertEquals(game.playerOneDeviation, response.get("$.data.getGames.edges[$index].node.playerOneDeviation", Int::class.java))
            assertEquals(game.playerOneRating, response.get("$.data.getGames.edges[$index].node.playerOneRating", Int::class.java))
            assertEquals(game.playerTwoId.toString(), response.get("$.data.getGames.edges[$index].node.playerTwoId"))
            assertEquals(game.playerTwoName, response.get("$.data.getGames.edges[$index].node.playerTwoName"))
            assertEquals(game.playerTwoDeviation, response.get("$.data.getGames.edges[$index].node.playerTwoDeviation", Int::class.java))
            assertEquals(game.playerTwoRating, response.get("$.data.getGames.edges[$index].node.playerTwoRating", Int::class.java))
            assertEquals(game.result!!.playerOneScore, response.get("$.data.getGames.edges[$index].node.result.playerOneScore", Int::class.java))
            assertEquals(game.result!!.playerOneDeviationDelta, response.get("$.data.getGames.edges[$index].node.result.playerOneDeviationDelta", Int::class.java))
            assertEquals(game.result!!.playerOneRatingDelta, response.get("$.data.getGames.edges[$index].node.result.playerOneRatingDelta", Int::class.java))
            assertEquals(game.result!!.playerTwoScore, response.get("$.data.getGames.edges[$index].node.result.playerTwoScore", Int::class.java))
            assertEquals(game.result!!.playerTwoDeviationDelta, response.get("$.data.getGames.edges[$index].node.result.playerTwoDeviationDelta", Int::class.java))
            assertEquals(game.result!!.playerTwoRatingDelta, response.get("$.data.getGames.edges[$index].node.result.playerTwoRatingDelta", Int::class.java))
        }
    }
}