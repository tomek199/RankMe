package com.tm.rankme.api.query.league

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.api.query.Item
import com.tm.rankme.api.query.Page
import com.tm.rankme.api.query.game.CompletedGame
import com.tm.rankme.api.query.game.Game
import com.tm.rankme.api.query.game.Result
import com.tm.rankme.api.query.game.ScheduledGame
import com.tm.rankme.api.query.player.Player
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
internal class LeagueQueryIntegrationTest {
    @Autowired
    private lateinit var template: GraphQLTestTemplate
    @Value("\${gateway.url}")
    private lateinit var url: String
    @MockkBean
    private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `Should return league`() {
        // given
        val league = League("xAeksxNOS-lq5mnKmm1tk", "Transformers", true, 3)
        val players = listOf(
            Player(randomNanoId(), "Optimus Prime", 145, 2746),
        )
        val completedGames = List(2) {
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
        val scheduledGames = List(1) {
            ScheduledGame(
                randomNanoId(), LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val games: List<Game> = completedGames + scheduledGames
        val page = Page(games.map { Item(it, it.id) }, false, true)
        every {
            restTemplate.getForObject("$url/query-service/leagues/${league.id}", League::class.java)
        } returns league
        every {
            restTemplate.exchange("$url/query-service/leagues/${league.id}/players",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(players))
        every {
            restTemplate.exchange("$url/query-service/leagues/${league.id}/games?first=3",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(league.id, response.get("$.data.league.id"))
        assertEquals(league.name, response.get("$.data.league.name"))
        assertEquals(league.allowDraws, response.get("$.data.league.allowDraws", Boolean::class.java))
        assertEquals(league.maxScore, response.get("$.data.league.maxScore", Int::class.java))
        assertEquals(players.first().id, response.get("$.data.league.players[0].id"))
        assertEquals(players.first().name, response.get("$.data.league.players[0].name"))
        assertEquals(players.first().deviation, response.get("$.data.league.players[0].deviation").toInt())
        assertEquals(players.first().rating, response.get("$.data.league.players[0].rating").toInt())
        assertEquals(page.hasPreviousPage, response.get("$.data.league.games.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.league.games.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.league.games.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.league.games.pageInfo.endCursor"))

        games.forEachIndexed {index, game ->
            assertEquals(game.id, response.get("$.data.league.games.edges[$index].cursor"))
            assertEquals(game.id, response.get("$.data.league.games.edges[$index].node.id"))
            assertEquals(game.dateTime.toString(), response.get("$.data.league.games.edges[$index].node.dateTime"))
            assertEquals(game.playerOneId, response.get("$.data.league.games.edges[$index].node.playerOneId"))
            assertEquals(game.playerOneName, response.get("$.data.league.games.edges[$index].node.playerOneName"))
            assertEquals(game.playerOneDeviation, response.get("$.data.league.games.edges[$index].node.playerOneDeviation", Int::class.java))
            assertEquals(game.playerOneRating, response.get("$.data.league.games.edges[$index].node.playerOneRating", Int::class.java))
            assertEquals(game.playerTwoId, response.get("$.data.league.games.edges[$index].node.playerTwoId"))
            assertEquals(game.playerTwoName, response.get("$.data.league.games.edges[$index].node.playerTwoName"))
            assertEquals(game.playerTwoDeviation, response.get("$.data.league.games.edges[$index].node.playerTwoDeviation", Int::class.java))
            assertEquals(game.playerTwoRating, response.get("$.data.league.games.edges[$index].node.playerTwoRating", Int::class.java))
            if (game is CompletedGame) {
                assertEquals(game.result.playerOneScore, response.get("$.data.league.games.edges[$index].node.result.playerOneScore", Int::class.java))
                assertEquals(game.result.playerOneDeviationDelta, response.get("$.data.league.games.edges[$index].node.result.playerOneDeviationDelta", Int::class.java))
                assertEquals(game.result.playerOneRatingDelta, response.get("$.data.league.games.edges[$index].node.result.playerOneRatingDelta", Int::class.java))
                assertEquals(game.result.playerTwoScore, response.get("$.data.league.games.edges[$index].node.result.playerTwoScore", Int::class.java))
                assertEquals(game.result.playerTwoDeviationDelta, response.get("$.data.league.games.edges[$index].node.result.playerTwoDeviationDelta", Int::class.java))
                assertEquals(game.result.playerTwoRatingDelta, response.get("$.data.league.games.edges[$index].node.result.playerTwoRatingDelta", Int::class.java))
            }
        }
    }

    @Test
    internal fun `Should return leagues`() {
        // given
        val leagues = List(8) {
            League(randomNanoId(), "League-${Random.nextInt()}", Random.nextBoolean(), Random.nextInt(10))
        }
        val page = Page(leagues.map { Item(it, it.id) }, true, true)
        every {
            restTemplate.exchange("$url/query-service/leagues?first=8&after=MTY0MDExNzM5MDA0MA==",
                HttpMethod.GET, null, ofType(ParameterizedTypeReference::class))
        } returns ResponseEntity.of(Optional.of(page))
        val request = "graphql/leagues.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(page.hasPreviousPage, response.get("$.data.leagues.pageInfo.hasPreviousPage", Boolean::class.java))
        assertEquals(page.hasNextPage, response.get("$.data.leagues.pageInfo.hasNextPage", Boolean::class.java))
        assertEquals(page.items.first().cursor, response.get("$.data.leagues.pageInfo.startCursor"))
        assertEquals(page.items.last().cursor, response.get("$.data.leagues.pageInfo.endCursor"))
        leagues.forEachIndexed { index, league ->
            assertEquals(league.id, response.get("$.data.leagues.edges[$index].cursor"))
            assertEquals(league.id, response.get("$.data.leagues.edges[$index].node.id"))
            assertEquals(league.name, response.get("$.data.leagues.edges[$index].node.name"))
            assertEquals(league.allowDraws, response.get("$.data.leagues.edges[$index].node.allowDraws", Boolean::class.java))
            assertEquals(league.maxScore, response.get("$.data.leagues.edges[$index].node.maxScore", Int::class.java))
        }
    }
}