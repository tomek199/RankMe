package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.mutation.CompleteGame
import com.tm.rankme.e2e.mutation.CreatePlayer
import com.tm.rankme.e2e.mutation.PlayGame
import com.tm.rankme.e2e.mutation.ScheduleGame
import com.tm.rankme.e2e.query.GetPlayer
import com.tm.rankme.e2e.util.ApplicationContext
import io.cucumber.java8.En
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.test.*

class PlayerSteps(
    private val graphQlClient: GraphQLKtorClient,
    private val context: ApplicationContext,
    @Value("\${cucumber.step-delay}") private val stepDelay: Long
) : En {

    init {
        Given("I create player {string}") {
                playerName: String ->
            runBlocking {
                val mutation = CreatePlayer(context.leagueId(), playerName)
                val result = graphQlClient.execute(mutation)
                assertEquals(status, result.data?.createPlayer)
                context.update()
                delay(stepDelay)
            }
        }

        When("I play game between {string} and {string} with result {int} : {int}") {
                playerOneName: String, playerTwoName: String, playerOneScore: Int, playerTwoScore: Int ->
            runBlocking {
                playGame(playerOneName, playerTwoName, playerOneScore, playerTwoScore)
                delay(stepDelay)
            }
        }

        When("I play {int} games between {string} and {string}") {
                numberOfGames: Int, playerOneName: String, playerTwoName: String ->
            runBlocking {
                repeat(numberOfGames) {
                    playGame(playerOneName, playerTwoName, Random.nextInt(10), Random.nextInt(10))
                }
                delay(stepDelay)
            }
        }

        When("I schedule game between {string} and {string} in {int} hours") {
                playerOneName: String, playerTwoName: String, hours: Int ->
            runBlocking {
                scheduleGame(playerOneName, playerTwoName, hours)
                delay(stepDelay)
            }
        }

        When("I schedule {int} games between {string} and {string} in {int} hours") {
                numberOfGames: Int, playerOneName: String, playerTwoName: String, hours: Int ->
            runBlocking {
                repeat(numberOfGames) {
                    scheduleGame(playerOneName, playerTwoName, hours + it)
                }
                delay(stepDelay)
            }
        }

        When("I complete game between {string} and {string} with result {int} : {int}") {
                playerOneName: String, playerTwoName: String, playerOneScore: Int, playerTwoScore: Int ->
            runBlocking {
                val playerOneId = context.playerId(playerOneName)
                val query = GetPlayer(playerOneId, 1)
                val gameId = graphQlClient.execute(query).data?.player?.games?.edges?.first { edge ->
                    edge.node.playerOneName == playerOneName && edge.node.playerTwoName == playerTwoName
                } ?.node?.id ?: fail("Cannot get scheduled game for players $playerOneName and $playerTwoName")
                val mutation = CompleteGame(gameId, playerOneScore, playerTwoScore)
                graphQlClient.execute(mutation).data?.let {
                    assertEquals(status, it.completeGame)
                } ?: fail("Cannot execute completeGame command")
                delay(stepDelay)
            }
        }

        Then("I have player {string} with deviation {int} and rating {int}") {
                name: String, deviation: Int, rating: Int ->
            runBlocking {
                val id = context.playerId(name)
                val query = GetPlayer(id)
                graphQlClient.execute(query).data?.let {
                    assertEquals(id, it.player.id)
                    assertEquals(name, it.player.name)
                    assertEquals(deviation, it.player.deviation)
                    assertEquals(rating, it.player.rating)
                } ?: fail("Cannot get player by id $id")
            }
        }

        Then("I have player {string} with first {int} of {int} games connected") {
                name: String, first: Int, of: Int ->
            runBlocking {
                val id = context.playerId(name)
                val query = GetPlayer(id, first)
                graphQlClient.execute(query).data?.let {
                    assertNotNull(it.player.games)
                    assertFalse(it.player.games.pageInfo.hasPreviousPage)
                    assertEquals(first < of, it.player.games.pageInfo.hasNextPage)
                    assertEquals(first, it.player.games.edges.size)
                    assertEquals(it.player.games.pageInfo.startCursor, it.player.games.edges.first().cursor)
                    assertEquals(it.player.games.pageInfo.endCursor, it.player.games.edges.last().cursor)
                    it.player.games.edges.forEach { edge ->
                        assertTrue(it.player.id == edge.node.playerOneId || it.player.id == edge.node.playerTwoId)
                    }
                } ?: fail("Cannot get player by id $id")
            }
        }
    }

    private suspend fun playGame(playerOneName: String, playerTwoName: String, playerOneScore: Int, playerTwoScore: Int) {
        val playerOneId = context.playerId(playerOneName)
        val playerTwoId = context.playerId(playerTwoName)
        val mutation = PlayGame(playerOneId, playerTwoId, playerOneScore, playerTwoScore)
        graphQlClient.execute(mutation).data?.let {
            assertEquals(status, it.playGame)
        } ?: fail("Cannot execute playGame command")
    }

    private suspend fun scheduleGame(playerOneName: String, playerTwoName: String, hours: Int) {
        val playerOneId = context.playerId(playerOneName)
        val playerTwoId = context.playerId(playerTwoName)
        val dateTime = LocalDateTime.now().plusHours(hours.toLong())
        val mutation = ScheduleGame(playerOneId, playerTwoId, dateTime)
        graphQlClient.execute(mutation).data?.let {
            assertEquals(status, it.scheduleGame)
        } ?: fail("Cannot execute scheduleGame command")
    }
}