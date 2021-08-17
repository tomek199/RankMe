package com.tm.rankme.api.query.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.api.query.Item
import com.tm.rankme.api.query.Page
import com.tm.rankme.api.query.game.Game
import com.tm.rankme.api.query.game.Result
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
        val games = List(3) {
            Game(
                randomNanoId(), LocalDateTime.now(),
                player.id, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100),
                    Random.nextInt(10), - Random.nextInt(50), Random.nextInt(100)
                )
            )
        }
        val page = Page(games.map { Item(it, it.id) }, false, true)
        every { restTemplate.getForObject("$url/query-service/players/${player.id}", Player::class.java) } returns player
        every {
            restTemplate.exchange("$url/query-service/players/${player.id}/games?first=3",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/get-player.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(player.id, response.get("$.data.getPlayer.id"))
        assertEquals(player.name, response.get("$.data.getPlayer.name"))
        assertEquals(player.deviation, response.get("$.data.getPlayer.deviation", Int::class.java))
        assertEquals(player.rating, response.get("$.data.getPlayer.rating", Int::class.java))

        games.forEachIndexed {index, game ->
            assertEquals(game.id, response.get("$.data.getPlayer.games.edges[$index].cursor"))
            assertEquals(game.id, response.get("$.data.getPlayer.games.edges[$index].node.id"))
            assertEquals(game.dateTime.toString(), response.get("$.data.getPlayer.games.edges[$index].node.dateTime"))
            assertEquals(game.playerOneId, response.get("$.data.getPlayer.games.edges[$index].node.playerOneId"))
            assertEquals(game.playerOneName, response.get("$.data.getPlayer.games.edges[$index].node.playerOneName"))
            assertEquals(game.playerOneDeviation, response.get("$.data.getPlayer.games.edges[$index].node.playerOneDeviation", Int::class.java))
            assertEquals(game.playerOneRating, response.get("$.data.getPlayer.games.edges[$index].node.playerOneRating", Int::class.java))
            assertEquals(game.playerTwoId, response.get("$.data.getPlayer.games.edges[$index].node.playerTwoId"))
            assertEquals(game.playerTwoName, response.get("$.data.getPlayer.games.edges[$index].node.playerTwoName"))
            assertEquals(game.playerTwoDeviation, response.get("$.data.getPlayer.games.edges[$index].node.playerTwoDeviation", Int::class.java))
            assertEquals(game.playerTwoRating, response.get("$.data.getPlayer.games.edges[$index].node.playerTwoRating", Int::class.java))
            assertEquals(game.result!!.playerOneScore, response.get("$.data.getPlayer.games.edges[$index].node.result.playerOneScore", Int::class.java))
            assertEquals(game.result!!.playerOneDeviationDelta, response.get("$.data.getPlayer.games.edges[$index].node.result.playerOneDeviationDelta", Int::class.java))
            assertEquals(game.result!!.playerOneRatingDelta, response.get("$.data.getPlayer.games.edges[$index].node.result.playerOneRatingDelta", Int::class.java))
            assertEquals(game.result!!.playerTwoScore, response.get("$.data.getPlayer.games.edges[$index].node.result.playerTwoScore", Int::class.java))
            assertEquals(game.result!!.playerTwoDeviationDelta, response.get("$.data.getPlayer.games.edges[$index].node.result.playerTwoDeviationDelta", Int::class.java))
            assertEquals(game.result!!.playerTwoRatingDelta, response.get("$.data.getPlayer.games.edges[$index].node.result.playerTwoRatingDelta", Int::class.java))
        }
    }
}