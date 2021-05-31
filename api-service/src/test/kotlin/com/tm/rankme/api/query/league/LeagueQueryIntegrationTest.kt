package com.tm.rankme.api.query.league

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.api.query.Item
import com.tm.rankme.api.query.Page
import com.tm.rankme.api.query.game.Game
import com.tm.rankme.api.query.game.Result
import com.tm.rankme.api.query.player.Player
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
internal class LeagueQueryIntegrationTest {
    @Autowired
    private lateinit var template: GraphQLTestTemplate
    @MockkBean
    private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `Should return league`() {
        // given
        val league = League(UUID.fromString("83222ad3-219c-48b0-bcc2-817efb61cfda"), "Transformers", true, 3)
        val players = listOf(
            Player(UUID.randomUUID(), "Optimus Prime", 145, 2746),
        )
        val games = List(3) {
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
            restTemplate.getForObject("http://localhost:9700/query-service/leagues/${league.id}", League::class.java)
        } returns league
        every {
            restTemplate.exchange("http://localhost:9700/query-service/players?leagueId=${league.id}",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(players))
        every {
            restTemplate.exchange("http://localhost:9700/query-service/games?leagueId=${league.id}&first=3",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/get-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(league.id.toString(), response.get("$.data.getLeague.id"))
        assertEquals(league.name, response.get("$.data.getLeague.name"))
        assertEquals(league.allowDraws, response.get("$.data.getLeague.allowDraws", Boolean::class.java))
        assertEquals(league.maxScore, response.get("$.data.getLeague.maxScore", Int::class.java))
        assertEquals(players[0].id.toString(), response.get("$.data.getLeague.players[0].id"))
        assertEquals(players[0].name, response.get("$.data.getLeague.players[0].name"))
        assertEquals(players[0].deviation, response.get("$.data.getLeague.players[0].deviation").toInt())
        assertEquals(players[0].rating, response.get("$.data.getLeague.players[0].rating").toInt())
        assertEquals(page.hasPreviousPage, response.get("$.data.getLeague.games.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.getLeague.games.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items[0].cursor, response.get("$.data.getLeague.games.pageInfo.startCursor"))
        assertEquals(page.items[2].cursor, response.get("$.data.getLeague.games.pageInfo.endCursor"))

        games.forEachIndexed {index, game ->
            assertEquals(game.id.toString(), response.get("$.data.getLeague.games.edges[$index].cursor"))
            assertEquals(game.id.toString(), response.get("$.data.getLeague.games.edges[$index].node.id"))
            assertEquals(game.dateTime.toString(), response.get("$.data.getLeague.games.edges[$index].node.dateTime"))
            assertEquals(game.playerOneId.toString(), response.get("$.data.getLeague.games.edges[$index].node.playerOneId"))
            assertEquals(game.playerOneName, response.get("$.data.getLeague.games.edges[$index].node.playerOneName"))
            assertEquals(game.playerOneDeviation, response.get("$.data.getLeague.games.edges[$index].node.playerOneDeviation", Int::class.java))
            assertEquals(game.playerOneRating, response.get("$.data.getLeague.games.edges[$index].node.playerOneRating", Int::class.java))
            assertEquals(game.playerTwoId.toString(), response.get("$.data.getLeague.games.edges[$index].node.playerTwoId"))
            assertEquals(game.playerTwoName, response.get("$.data.getLeague.games.edges[$index].node.playerTwoName"))
            assertEquals(game.playerTwoDeviation, response.get("$.data.getLeague.games.edges[$index].node.playerTwoDeviation", Int::class.java))
            assertEquals(game.playerTwoRating, response.get("$.data.getLeague.games.edges[$index].node.playerTwoRating", Int::class.java))
            assertEquals(game.result!!.playerOneScore, response.get("$.data.getLeague.games.edges[$index].node.result.playerOneScore", Int::class.java))
            assertEquals(game.result!!.playerOneDeviationDelta, response.get("$.data.getLeague.games.edges[$index].node.result.playerOneDeviationDelta", Int::class.java))
            assertEquals(game.result!!.playerOneRatingDelta, response.get("$.data.getLeague.games.edges[$index].node.result.playerOneRatingDelta", Int::class.java))
            assertEquals(game.result!!.playerTwoScore, response.get("$.data.getLeague.games.edges[$index].node.result.playerTwoScore", Int::class.java))
            assertEquals(game.result!!.playerTwoDeviationDelta, response.get("$.data.getLeague.games.edges[$index].node.result.playerTwoDeviationDelta", Int::class.java))
            assertEquals(game.result!!.playerTwoRatingDelta, response.get("$.data.getLeague.games.edges[$index].node.result.playerTwoRatingDelta", Int::class.java))
        }
    }
}