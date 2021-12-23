package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.db.DatabaseUtil
import com.tm.rankme.e2e.mutation.ChangeLeagueSettings
import com.tm.rankme.e2e.mutation.CreateLeague
import com.tm.rankme.e2e.mutation.RenameLeague
import com.tm.rankme.e2e.query.GetLeague
import com.tm.rankme.e2e.query.GetLeagues
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import kotlin.random.Random
import kotlin.test.*

class LeagueSteps(
    private val graphQlClient: GraphQLKtorClient,
    private val dbUtil: DatabaseUtil,
    @Value("\${cucumber.step-delay}") private val stepDelay: Long
) : En {

    init {
        Given("I create league {string}") { name: String ->
            runBlocking {
                delay(stepDelay)
                val mutation = CreateLeague(name)
                val result = graphQlClient.execute(mutation)
                assertEquals(status, result.data?.createLeague)
            }
        }

        Given("I create {int} leagues") { numberOfLeagues: Int ->
            runBlocking {
                delay(stepDelay)
                repeat(numberOfLeagues) {
                    val mutation = CreateLeague("League ${Random.nextInt()}")
                    val result = graphQlClient.execute(mutation)
                    assertEquals(status, result.data?.createLeague)
                }
            }
        }

        When("I change league {string} settings to allow draws {} and max score {int}") { name: String, allowDraws: Boolean, maxScore: Int ->
            runBlocking {
                delay(stepDelay)
                val id = dbUtil.leagueIdByName(name)
                val mutation = ChangeLeagueSettings(id, allowDraws, maxScore)
                val result = graphQlClient.execute(mutation)
                assertEquals(status, result.data?.changeLeagueSettings)
            }
        }

        When("I rename league {string} to {string}") { name: String, newName: String ->
            runBlocking {
                delay(stepDelay)
                val id = dbUtil.leagueIdByName(name)
                val mutation = RenameLeague(id, newName)
                val result = graphQlClient.execute(mutation)
                assertEquals(status, result.data?.renameLeague)
            }
        }

        Then("I have league {string} with allow draws {} and max score {int}") { name: String, allowDraws: Boolean, maxScore: Int ->
            runBlocking {
                delay(stepDelay)
                val id = dbUtil.leagueIdByName(name)
                val query = GetLeague(id)
                graphQlClient.execute(query).data?.let {
                    assertEquals(id, it.league.id)
                    assertEquals(name, it.league.name)
                    assertEquals(allowDraws, it.league.allowDraws)
                    assertEquals(maxScore, it.league.maxScore)
                } ?: fail("Cannot get league by id $id")
            }
        }

        Then("I have players in league {string}:") { name: String, playersTable: DataTable ->
            runBlocking {
                delay(stepDelay)
                val id = dbUtil.leagueIdByName(name)
                val query = GetLeague(id)
                graphQlClient.execute(query).data?.let {
                    val expectedPlayers = playersTable.asMaps()
                    expectedPlayers.forEachIndexed { index, expectedPlayer ->
                        val player = it.league.players?.get(index) ?: fail("Cannot get player on index $index")
                        assertEquals(expectedPlayer["name"], player.name)
                        assertEquals(expectedPlayer["deviation"]?.toInt(), player.deviation)
                        assertEquals(expectedPlayer["rating"]?.toInt(), player.rating)
                    }
                } ?: fail("Cannot get league by id $id")
            }
        }

        Then("I have first {int} of {int} games connected in league {string}") {
                first: Int, of: Int, name: String ->
            runBlocking {
                delay(stepDelay)
                val id = dbUtil.leagueIdByName(name)
                val query = GetLeague(id, first)
                graphQlClient.execute(query).data?.let {
                    assertNotNull(it.league.games)
                    assertFalse(it.league.games.pageInfo.hasPreviousPage)
                    assertEquals(first < of, it.league.games.pageInfo.hasNextPage)
                    assertEquals(first, it.league.games.edges.size)
                    assertEquals(it.league.games.pageInfo.startCursor, it.league.games.edges.first().cursor)
                    assertEquals(it.league.games.pageInfo.endCursor, it.league.games.edges.last().cursor)
                    it.league.games.edges.forEach { edge ->
                        it.league.players?.map { player -> player.id } ?.toList()
                            .also { players -> players?.contains(edge.node.playerOneId) }
                            .also { players -> players?.contains(edge.node.playerTwoId) }
                            ?: fail("Cannot get players for league ${it.league.name}")
                    }
                } ?: fail("Cannot get league by id $id")
            }
        }
        Then("I have first {int} of {int} leagues listed") {
                first: Int, of: Int ->
            runBlocking {
                delay(stepDelay)
                val cursors = allLeaguesCursors(of)
                val query = GetLeagues(first)
                graphQlClient.execute(query).data?.let {
                    assertFalse(it.leagues.pageInfo.hasPreviousPage)
                    assertEquals(of > first, it.leagues.pageInfo.hasNextPage)
                    assertEquals(cursors.first(), it.leagues.pageInfo.startCursor)
                    assertEquals(cursors[first - 1], it.leagues.pageInfo.endCursor)
                    it.leagues.edges.forEachIndexed { index, edge -> assertEquals(cursors[index], edge.cursor) }
                } ?: fail("Leagues not found first=$first of=$of")
            }
        }

        Then("I have first {int} after {int} of {int} leagues listed") {
                first: Int, after: Int, of: Int ->
            runBlocking {
                delay(stepDelay)
                val cursors = allLeaguesCursors(of)
                val query = GetLeagues(first, cursors[after - 1])
                graphQlClient.execute(query).data?.let {
                    assertTrue(it.leagues.pageInfo.hasPreviousPage)
                    assertEquals(of > first + after, it.leagues.pageInfo.hasNextPage)
                    assertEquals(cursors[after], it.leagues.pageInfo.startCursor)
                    assertEquals(cursors[after + first - 1], it.leagues.pageInfo.endCursor)
                    it.leagues.edges.forEachIndexed { index, edge -> assertEquals(cursors[after + index], edge.cursor) }
                } ?: fail("Leagues not found first=$first after=$after of=$of")
            }
        }
    }

    private suspend fun allLeaguesCursors(of: Int): List<String> {
        val query = GetLeagues(of)
        val allResults = graphQlClient.execute(query)
        return allResults.data?.leagues?.edges?.map { it.cursor } ?.toList() ?: fail("Leagues cursors not found")
    }
}