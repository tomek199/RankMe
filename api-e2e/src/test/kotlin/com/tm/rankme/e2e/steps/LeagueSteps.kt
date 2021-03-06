package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.db.DatabaseUtil
import com.tm.rankme.e2e.mutation.ChangeLeagueSettings
import com.tm.rankme.e2e.mutation.CreateLeague
import com.tm.rankme.e2e.mutation.RenameLeague
import com.tm.rankme.e2e.query.GetLeague
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.fail

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
                    assertEquals(id, it.getLeague.id)
                    assertEquals(name, it.getLeague.name)
                    assertEquals(allowDraws, it.getLeague.allowDraws)
                    assertEquals(maxScore, it.getLeague.maxScore)
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
                        val player = it.getLeague.players[index]
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
                    assertNotNull(it.getLeague.games)
                    assertFalse(it.getLeague.games.pageInfo.hasPreviousPage)
                    assertEquals(first < of, it.getLeague.games.pageInfo.hasNextPage)
                    assertEquals(first, it.getLeague.games.edges.size)
                    assertEquals(it.getLeague.games.pageInfo.startCursor, it.getLeague.games.edges.first().cursor)
                    assertEquals(it.getLeague.games.pageInfo.endCursor, it.getLeague.games.edges.last().cursor)
                    it.getLeague.games.edges.forEach { edge ->
                        it.getLeague.players.map { player -> player.id }.toList()
                            .also { players -> players.contains(edge.node.playerOneId) }
                            .also { players -> players.contains(edge.node.playerTwoId) }
                    }
                } ?: fail("Cannot get league by id $id")
            }
        }
    }
}