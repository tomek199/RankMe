package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.db.DatabaseUtil
import com.tm.rankme.e2e.query.GetGames
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class GameSteps(
    private val graphQlClient: GraphQLKtorClient,
    private val dbUtil: DatabaseUtil,
    @Value("\${cucumber.step-delay}") private val stepDelay: Long
) : En {

    init {
        Then("I have first {int} of {int} games listed for league {string}") {
                first: Int, of: Int, leagueName: String ->
            runBlocking {
                delay(stepDelay)
                val leagueId = dbUtil.leagueIdByName(leagueName)
                val cursors = allGamesCursors(leagueId, of)
                val query = GetGames(leagueId, first)
                val result = graphQlClient.execute(query)
                result.data?.let {
                    assertFalse(it.getGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first, it.getGames.pageInfo.hasNextPage)
                    assertEquals(cursors.first(), it.getGames.pageInfo.startCursor)
                    assertEquals(cursors[first - 1], it.getGames.pageInfo.endCursor)
                    it.getGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[index], edge.cursor) }
                } ?: fail("Games not found first=$first of=$of")
            }
        }

        Then("I have first {int} after {int} of {int} games listed for league {string}") {
                first: Int, after: Int, of: Int, leagueName: String ->
            runBlocking {
                delay(stepDelay)
                val leagueId = dbUtil.leagueIdByName(leagueName)
                val cursors = allGamesCursors(leagueId, of)
                val query = GetGames(leagueId, first, cursors[after - 1])
                val result = graphQlClient.execute(query)
                result.data?.let {
                    assertTrue(it.getGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first + after, it.getGames.pageInfo.hasNextPage)
                    assertEquals(cursors[after], it.getGames.pageInfo.startCursor)
                    assertEquals(cursors[after + first - 1], it.getGames.pageInfo.endCursor)
                    it.getGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[after + index], edge.cursor) }
                } ?: fail("Games not found first=$first of=$of")
            }
        }

        Then("I have {int} games in league {string}:") {
                first: Int, leagueName: String, gamesTable: DataTable ->
            runBlocking {
                delay(stepDelay)
                val leagueId = dbUtil.leagueIdByName(leagueName)
                val query = GetGames(leagueId, first)
                graphQlClient.execute(query).data?.let {
                    val expectedGames = gamesTable.asMaps()
                    expectedGames.forEachIndexed { index, expectedGame ->
                        val game = it.getGames.edges[index].node
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
    }

    private suspend fun allGamesCursors(leagueId: String, of: Int): List<String> {
        val query = GetGames(leagueId, of)
        val allResults = graphQlClient.execute(query)
        return allResults.data?.getGames?.edges?.map { it.cursor }?.toList() ?: fail("Games cursors not found")
    }
}