package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.query.GetPlayerCompletedGames
import com.tm.rankme.e2e.query.GetPlayerGames
import com.tm.rankme.e2e.query.GetPlayerScheduledGames
import com.tm.rankme.e2e.query.RequestCursor
import com.tm.rankme.e2e.util.ApplicationContext
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class PlayerGamesSteps(
    private val graphQlClient: GraphQLKtorClient,
    private val context: ApplicationContext,
    @Value("\${cucumber.step-delay}") private val stepDelay: Long
) : En {

    init {
        Then("I have first {int} of {int} games listed for player {string}") {
                first: Int, of: Int, player: String ->
            runBlocking {
                val playerId = context.playerId(player)
                val cursors = allPlayerGamesCursors(playerId, of)
                val query = GetPlayerGames(playerId, first)
                graphQlClient.execute(query).data?.let {
                    assertFalse(it.playerGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first, it.playerGames.pageInfo.hasNextPage)
                    assertEquals(cursors.first(), it.playerGames.pageInfo.startCursor)
                    assertEquals(cursors[first - 1], it.playerGames.pageInfo.endCursor)
                    it.playerGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[index], edge.cursor) }
                } ?: fail("Player games not found first=$first of=$of player=$player")
            }
        }

        Then("I have first {int} after {int} of {int} games listed for player {string}") {
                first: Int, after: Int, of: Int, player: String ->
            runBlocking {
                val playerId = context.playerId(player)
                val cursors = allPlayerGamesCursors(playerId, of)
                val query = GetPlayerGames(playerId, first, RequestCursor(RequestCursor.Direction.AFTER, cursors[after - 1]))
                graphQlClient.execute(query).data?.let {
                    assertTrue(it.playerGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first + after, it.playerGames.pageInfo.hasNextPage)
                    assertEquals(cursors[after], it.playerGames.pageInfo.startCursor)
                    assertEquals(cursors[after + first - 1], it.playerGames.pageInfo.endCursor)
                    it.playerGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[after + index], edge.cursor) }
                } ?: fail("Player games not found first=$first after=$after of=$of player=$player")
            }
        }

        Then("I have first {int} before {int} of {int} games listed for player {string}") {
                first: Int, before: Int, of: Int, player: String ->
            runBlocking {
                val playerId = context.playerId(player)
                val cursors = allPlayerGamesCursors(playerId, of)
                val query = GetPlayerGames(playerId, first, RequestCursor(RequestCursor.Direction.BEFORE, cursors[before - 1]))
                graphQlClient.execute(query).data?.let {
                    assertEquals(first + 1 < before, it.playerGames.pageInfo.hasPreviousPage)
                    assertTrue(it.playerGames.pageInfo.hasNextPage)
                    assertEquals(cursors[before - first -1], it.playerGames.pageInfo.startCursor)
                    assertEquals(cursors[before - 2], it.playerGames.pageInfo.endCursor)
                    it.playerGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[before - first - 1 + index], edge.cursor) }
                } ?: fail("Player games not found first=$first before=$before of=$of player=$player")
            }
        }

        Then("I have first {int} of {int} completed games listed for player {string}") {
                first: Int, of: Int, player: String ->
            runBlocking {
                val playerId = context.playerId(player)
                val cursors = allPlayerCompletedGamesCursors(playerId, of)
                val query = GetPlayerCompletedGames(playerId, first)
                graphQlClient.execute(query).data?.let {
                    assertFalse(it.playerCompletedGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first, it.playerCompletedGames.pageInfo.hasNextPage)
                    assertEquals(cursors.first(), it.playerCompletedGames.pageInfo.startCursor)
                    assertEquals(cursors[first - 1], it.playerCompletedGames.pageInfo.endCursor)
                    it.playerCompletedGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[index], edge.cursor) }
                } ?: fail("Player completed games not found first=$first of=$of")
            }
        }

        Then("I have first {int} after {int} of {int} completed games listed for player {string}") {
                first: Int, after: Int, of: Int, player: String ->
            runBlocking {
                val playerId = context.playerId(player)
                val cursors = allPlayerCompletedGamesCursors(playerId, of)
                val query = GetPlayerCompletedGames(playerId, first, RequestCursor(RequestCursor.Direction.AFTER, cursors[after - 1]))
                graphQlClient.execute(query).data?.let {
                    assertTrue(it.playerCompletedGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first + after, it.playerCompletedGames.pageInfo.hasNextPage)
                    assertEquals(cursors[after], it.playerCompletedGames.pageInfo.startCursor)
                    assertEquals(cursors[after + first - 1], it.playerCompletedGames.pageInfo.endCursor)
                    it.playerCompletedGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[after + index], edge.cursor) }
                } ?: fail("Player completed games not found first=$first after=$after of=$of")
            }
        }

        Then("I have first {int} before {int} of {int} completed games listed for player {string}") {
                first: Int, before: Int, of: Int, player: String ->
            runBlocking {
                val playerId = context.playerId(player)
                val cursors = allPlayerCompletedGamesCursors(playerId, of)
                val query = GetPlayerCompletedGames(playerId, first, RequestCursor(RequestCursor.Direction.BEFORE, cursors[before - 1]))
                graphQlClient.execute(query).data?.let {
                    assertEquals(first + 1 < before, it.playerCompletedGames.pageInfo.hasPreviousPage)
                    assertTrue(it.playerCompletedGames.pageInfo.hasNextPage)
                    assertEquals(cursors[before - first -1], it.playerCompletedGames.pageInfo.startCursor)
                    assertEquals(cursors[before - 2], it.playerCompletedGames.pageInfo.endCursor)
                    it.playerCompletedGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[before - first - 1 + index], edge.cursor) }
                } ?: fail("Player completed games not found first=$first before=$before of=$of")
            }
        }

        Then("I have first {int} of {int} scheduled games listed for player {string}") {
                first: Int, of: Int, player: String ->
            runBlocking {
                val playerId = context.playerId(player)
                val cursors = allPlayerScheduledGamesCursors(playerId, of)
                val query = GetPlayerScheduledGames(playerId, first)
                graphQlClient.execute(query).data?.let {
                    assertFalse(it.playerScheduledGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first, it.playerScheduledGames.pageInfo.hasNextPage)
                    assertEquals(cursors.first(), it.playerScheduledGames.pageInfo.startCursor)
                    assertEquals(cursors[first - 1], it.playerScheduledGames.pageInfo.endCursor)
                    it.playerScheduledGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[index], edge.cursor) }
                } ?: fail("Player scheduled games not found first=$first of=$of")
            }
        }

        Then("I have first {int} after {int} of {int} scheduled games listed for player {string}") {
                first: Int, after: Int, of: Int, player: String ->
            runBlocking {
                val playerId = context.playerId(player)
                val cursors = allPlayerScheduledGamesCursors(playerId, of)
                val query = GetPlayerScheduledGames(playerId, first, RequestCursor(RequestCursor.Direction.AFTER, cursors[after - 1]))
                graphQlClient.execute(query).data?.let {
                    assertTrue(it.playerScheduledGames.pageInfo.hasPreviousPage)
                    assertEquals(of > first + after, it.playerScheduledGames.pageInfo.hasNextPage)
                    assertEquals(cursors[after], it.playerScheduledGames.pageInfo.startCursor)
                    assertEquals(cursors[after + first - 1], it.playerScheduledGames.pageInfo.endCursor)
                    it.playerScheduledGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[after + index], edge.cursor) }
                } ?: fail("Player scheduled games not found first=$first after=$after of=$of")
            }
        }

        Then("I have first {int} before {int} of {int} scheduled games listed for player {string}") {
                first: Int, before: Int, of: Int, player: String ->
            runBlocking {
                val playerId = context.playerId(player)
                val cursors = allPlayerScheduledGamesCursors(playerId, of)
                val query = GetPlayerScheduledGames(playerId, first, RequestCursor(RequestCursor.Direction.BEFORE, cursors[before - 1]))
                graphQlClient.execute(query).data?.let {
                    assertEquals(first + 1 < before, it.playerScheduledGames.pageInfo.hasPreviousPage)
                    assertTrue(it.playerScheduledGames.pageInfo.hasNextPage)
                    assertEquals(cursors[before - first -1], it.playerScheduledGames.pageInfo.startCursor)
                    assertEquals(cursors[before - 2], it.playerScheduledGames.pageInfo.endCursor)
                    it.playerScheduledGames.edges.forEachIndexed { index, edge -> assertEquals(cursors[before - first - 1 + index], edge.cursor) }
                } ?: fail("Player scheduled games not found first=$first before=$before of=$of")
            }
        }
    }

    private suspend fun allPlayerGamesCursors(playerId: String, of: Int): List<String> {
        val query = GetPlayerGames(playerId, of)
        val allResults = graphQlClient.execute(query)
        assertFalse(allResults.data!!.playerGames.pageInfo.hasPreviousPage)
        assertFalse(allResults.data!!.playerGames.pageInfo.hasNextPage)
        return allResults.data?.playerGames?.edges?.map { it.cursor } ?.toList() ?: fail("Player games cursors not found")
    }

    private suspend fun allPlayerCompletedGamesCursors(playerId: String, of: Int): List<String> {
        val query = GetPlayerCompletedGames(playerId, of)
        val allResults = graphQlClient.execute(query)
        assertFalse(allResults.data!!.playerCompletedGames.pageInfo.hasPreviousPage)
        assertFalse(allResults.data!!.playerCompletedGames.pageInfo.hasNextPage)
        return allResults.data?.playerCompletedGames?.edges?.map { it.cursor } ?.toList() ?: fail("Player completed games cursors not found")
    }

    private suspend fun allPlayerScheduledGamesCursors(playerId: String, of: Int): List<String> {
        val query = GetPlayerScheduledGames(playerId, of)
        val allResults = graphQlClient.execute(query)
        assertFalse(allResults.data!!.playerScheduledGames.pageInfo.hasPreviousPage)
        assertFalse(allResults.data!!.playerScheduledGames.pageInfo.hasNextPage)
        return allResults.data?.playerScheduledGames?.edges?.map { it.cursor } ?.toList() ?: fail("Player scheduled games cursors not found")
    }
}