package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.query.GetCompletedGames
import com.tm.rankme.e2e.query.GetGames
import com.tm.rankme.e2e.query.GetScheduledGames
import com.tm.rankme.e2e.util.ApplicationContext
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import kotlin.test.*

class GameSteps(
    private val graphQlClient: GraphQLKtorClient,
    private val context: ApplicationContext,
    @Value("\${cucumber.step-delay}") private val stepDelay: Long
) : En {

    init {
        Then("I have first {int} of {int} games listed") {
                first: Int, of: Int ->
            runBlocking {
                delay(stepDelay)
                val leagueId = context.leagueId()
                val cursors = allGamesCursors(leagueId, of)
                val query = GetGames(leagueId, first)
                graphQlClient.execute(query).data?.let {
                    assertFalse(it.games.pageInfo.hasPreviousPage)
                    assertEquals(of > first, it.games.pageInfo.hasNextPage)
                    assertEquals(cursors.first(), it.games.pageInfo.startCursor)
                    assertEquals(cursors[first - 1], it.games.pageInfo.endCursor)
                    it.games.edges.forEachIndexed { index, edge -> assertEquals(cursors[index], edge.cursor) }
                } ?: fail("Games not found first=$first of=$of")
            }
        }

        Then("I have first {int} after {int} of {int} games listed") {
                first: Int, after: Int, of: Int ->
            runBlocking {
                delay(stepDelay)
                val leagueId = context.leagueId()
                val cursors = allGamesCursors(leagueId, of)
                val query = GetGames(leagueId, first, cursors[after - 1])
                graphQlClient.execute(query).data?.let {
                    assertTrue(it.games.pageInfo.hasPreviousPage)
                    assertEquals(of > first + after, it.games.pageInfo.hasNextPage)
                    assertEquals(cursors[after], it.games.pageInfo.startCursor)
                    assertEquals(cursors[after + first - 1], it.games.pageInfo.endCursor)
                    it.games.edges.forEachIndexed { index, edge -> assertEquals(cursors[after + index], edge.cursor) }
                } ?: fail("Games not found first=$first after=$after of=$of")
            }
        }

        Then("I have first {int} of {int} completed games listed") {
                first: Int, of: Int ->
            runBlocking {
                delay(stepDelay)
                val leagueId = context.leagueId()
                val cursors = allCompletedGamesCursors(leagueId, of)
                val query = GetCompletedGames(leagueId, first)
                graphQlClient.execute(query).data?.let {
                    assertFalse(it.completedGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first, it.completedGames.pageInfo.hasNextPage)
                    assertEquals(cursors.first(), it.completedGames.pageInfo.startCursor)
                    assertEquals(cursors[first - 1], it.completedGames.pageInfo.endCursor)
                    it.completedGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[index], edge.cursor) }
                } ?: fail("Completed games not found first=$first of=$of")
            }
        }

        Then("I have first {int} after {int} of {int} completed games listed") {
                first: Int, after: Int, of: Int ->
            runBlocking {
                delay(stepDelay)
                val leagueId = context.leagueId()
                val cursors = allCompletedGamesCursors(leagueId, of)
                val query = GetCompletedGames(leagueId, first, cursors[after - 1])
                graphQlClient.execute(query).data?.let {
                    assertTrue(it.completedGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first + after, it.completedGames.pageInfo.hasNextPage)
                    assertEquals(cursors[after], it.completedGames.pageInfo.startCursor)
                    assertEquals(cursors[after + first - 1], it.completedGames.pageInfo.endCursor)
                    it.completedGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[after + index], edge.cursor) }
                } ?: fail("Completed games not found first=$first after=$after of=$of")
            }
        }

        Then("I have first {int} of {int} scheduled games listed") {
                first: Int, of: Int ->
            runBlocking {
                delay(stepDelay)
                val leagueId = context.leagueId()
                val cursors = allScheduledGamesCursors(leagueId, of)
                val query = GetScheduledGames(leagueId, first)
                graphQlClient.execute(query).data?.let {
                    assertFalse(it.scheduledGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first, it.scheduledGames.pageInfo.hasNextPage)
                    assertEquals(cursors.first(), it.scheduledGames.pageInfo.startCursor)
                    assertEquals(cursors[first - 1], it.scheduledGames.pageInfo.endCursor)
                    it.scheduledGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[index], edge.cursor) }
                } ?: fail("Scheduled games not found first=$first of=$of")
            }
        }

        Then("I have first {int} after {int} of {int} scheduled games listed") {
                first: Int, after: Int, of: Int ->
            runBlocking {
                delay(stepDelay)
                val leagueId = context.leagueId()
                val cursors = allScheduledGamesCursors(leagueId, of)
                val query = GetScheduledGames(leagueId, first, cursors[after - 1])
                graphQlClient.execute(query).data?.let {
                    assertTrue(it.scheduledGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first + after, it.scheduledGames.pageInfo.hasNextPage)
                    assertEquals(cursors[after], it.scheduledGames.pageInfo.startCursor)
                    assertEquals(cursors[after + first - 1], it.scheduledGames.pageInfo.endCursor)
                    it.scheduledGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[after + index], edge.cursor) }
                } ?: fail("Completed games not found first=$first after=$after of=$of")
            }
        }

        Then("I have {int} games:") {
                first: Int, gamesTable: DataTable ->
            runBlocking {
                delay(stepDelay)
                val leagueId = context.leagueId()
                val query = GetGames(leagueId, first)
                graphQlClient.execute(query).data?.let {
                    val expectedGames = gamesTable.asMaps()
                    expectedGames.forEachIndexed { index, expectedGame ->
                        val game = it.games.edges[index].node
                        assertEquals(expectedGame["firstName"], game.playerOneName)
                        assertEquals(expectedGame["firstScore"]?.toInt(), game.result?.playerOneScore)
                        assertEquals(expectedGame["firstRating"]?.toInt(), game.playerOneRating)
                        assertEquals(expectedGame["firstRatingDelta"]?.toInt(), game.result?.playerOneRatingDelta)
                        assertEquals(expectedGame["firstDeviation"]?.toInt(), game.playerOneDeviation)
                        assertEquals(expectedGame["firstDeviationDelta"]?.toInt(), game.result?.playerOneDeviationDelta)
                        assertEquals(expectedGame["secondName"], game.playerTwoName)
                        assertEquals(expectedGame["secondScore"]?.toInt(), game.result?.playerTwoScore)
                        assertEquals(expectedGame["secondRating"]?.toInt(), game.playerTwoRating)
                        assertEquals(expectedGame["secondRatingDelta"]?.toInt(), game.result?.playerTwoRatingDelta)
                        assertEquals(expectedGame["secondDeviation"]?.toInt(), game.playerTwoDeviation)
                        assertEquals(expectedGame["secondDeviationDelta"]?.toInt(), game.result?.playerTwoDeviationDelta)
                    }
                } ?: fail("Games not found first=$first")
            }
        }

        Then("I have no games") {
            runBlocking {
                delay(stepDelay)
                val leagueId = context.leagueId()
                val query = GetGames(leagueId, 1)
                graphQlClient.execute(query).data?.let {
                    assertFalse(it.games.pageInfo.hasPreviousPage)
                    assertFalse(it.games.pageInfo.hasNextPage)
                    assertNull(it.games.pageInfo.startCursor)
                    assertNull(it.games.pageInfo.endCursor)
                    assertTrue(it.games.edges.isEmpty())
                } ?: fail("No response data")
            }
        }
    }

    private suspend fun allGamesCursors(leagueId: String, of: Int): List<String> {
        val query = GetGames(leagueId, of)
        val allResults = graphQlClient.execute(query)
        return allResults.data?.games?.edges?.map { it.cursor } ?.toList() ?: fail("Games cursors not found")
    }

    private suspend fun allCompletedGamesCursors(leagueId: String, of: Int): List<String> {
        val query = GetCompletedGames(leagueId, of)
        val allResults = graphQlClient.execute(query)
        return allResults.data?.completedGames?.edges?.map { it.cursor } ?.toList() ?: fail("Games cursors not found")
    }

    private suspend fun allScheduledGamesCursors(leagueId: String, of: Int): List<String> {
        val query = GetScheduledGames(leagueId, of)
        val allResults = graphQlClient.execute(query)
        return allResults.data?.scheduledGames?.edges?.map { it.cursor } ?.toList() ?: fail("Games cursors not found")
    }
}