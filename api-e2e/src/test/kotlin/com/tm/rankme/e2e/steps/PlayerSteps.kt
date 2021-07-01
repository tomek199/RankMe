package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.db.DatabaseUtil
import com.tm.rankme.e2e.mutation.CreatePlayer
import com.tm.rankme.e2e.mutation.PlayGame
import com.tm.rankme.e2e.mutation.ScheduleGame
import com.tm.rankme.e2e.query.GetPlayer
import io.cucumber.java8.En
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.test.*

class PlayerSteps(
    private val graphQlClient: GraphQLKtorClient,
    private val dbUtil: DatabaseUtil,
    @Value("\${cucumber.step-delay}") private val stepDelay: Long
) : En {

    init {
        Given("I create player {string} in league {string}") {
                playerName: String, leagueName: String ->
            runBlocking {
                delay(stepDelay)
                val leagueId = dbUtil.leagueIdByName(leagueName)
                val mutation = CreatePlayer(leagueId, playerName)
                val result = graphQlClient.execute(mutation)
                assertEquals(status, result.data?.createPlayer)
            }
        }

        When("I play game between {string} and {string} with result {int} : {int}") {
                playerOneName: String, playerTwoName: String, playerOneScore: Int, playerTwoScore: Int ->
            runBlocking {
                delay(stepDelay)
                playGame(playerOneName, playerTwoName, playerOneScore, playerTwoScore)
            }
        }

        When("I play {int} games between {string} and {string}") {
                numberOfGames: Int, playerOneName: String, playerTwoName: String ->
            runBlocking {
                delay(stepDelay)
                repeat(numberOfGames) {
                    playGame(playerOneName, playerTwoName, Random.nextInt(10), Random.nextInt(10))
                }
            }
        }

        When("I schedule game between {string} and {string} in {int} hours") {
                playerOneName: String, playerTwoName: String, hours: Int ->
            runBlocking {
                delay(stepDelay)
                val playerOneId = dbUtil.playerIdByName(playerOneName)
                val playerTwoId = dbUtil.playerIdByName(playerTwoName)
                val dateTime = LocalDateTime.now().plusHours(hours.toLong())
                val mutation = ScheduleGame(playerOneId, playerTwoId, dateTime)
                graphQlClient.execute(mutation).data?.let {
                    assertEquals(status, it.scheduleGame)
                } ?: fail("Cannot execute command")
            }
        }

        Then("I should have player {string} with deviation {int} and rating {int}") {
                name: String, deviation: Int, rating: Int ->
            runBlocking {
                delay(stepDelay)
                val id = dbUtil.playerIdByName(name)
                val query = GetPlayer(id)
                graphQlClient.execute(query).data?.let {
                    assertEquals(id, it.getPlayer.id)
                    assertEquals(name, it.getPlayer.name)
                    assertEquals(deviation, it.getPlayer.deviation)
                    assertEquals(rating, it.getPlayer.rating)
                } ?: fail("Cannot get player by id $id")
            }
        }

        Then("I have player {string} with first {int} games of {int} connected") {
                name: String, first: Int, of: Int ->
            runBlocking {
                delay(stepDelay)
                val id = dbUtil.playerIdByName(name)
                val query = GetPlayer(id, first)
                graphQlClient.execute(query).data?.let {
                    assertNotNull(it.getPlayer.games)
                    assertFalse(it.getPlayer.games.pageInfo.hasPreviousPage)
                    assertEquals(first < of, it.getPlayer.games.pageInfo.hasNextPage)
                    assertEquals(first, it.getPlayer.games.edges.size)
                    assertEquals(it.getPlayer.games.pageInfo.startCursor, it.getPlayer.games.edges.first().cursor)
                    assertEquals(it.getPlayer.games.pageInfo.endCursor, it.getPlayer.games.edges.last().cursor)
                    it.getPlayer.games.edges.forEach { edge ->
                        assertTrue(it.getPlayer.id == edge.node.playerOneId || it.getPlayer.id == edge.node.playerTwoId)
                    }
                } ?: fail("Cannot get player by id $id")
            }
        }
    }

    private suspend fun playGame(playerOneName: String, playerTwoName: String, playerOneScore: Int, playerTwoScore: Int) {
        val playerOneId = dbUtil.playerIdByName(playerOneName)
        val playerTwoId = dbUtil.playerIdByName(playerTwoName)
        val mutation = PlayGame(playerOneId, playerTwoId, playerOneScore, playerTwoScore)
        graphQlClient.execute(mutation).data?.let {
            assertEquals(status, it.playGame)
        } ?: fail("Cannot execute command")
    }
}