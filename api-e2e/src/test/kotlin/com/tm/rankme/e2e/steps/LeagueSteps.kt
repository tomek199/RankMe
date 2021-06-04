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

        Then("I should have league {string} with allow draws {} and max score {int}") { name: String, allowDraws: Boolean, maxScore: Int ->
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
                    val players = playersTable.asMaps()
                    players.forEachIndexed { index, player ->
                        assertEquals(player["name"], it.getLeague.players[index].name)
                        assertEquals(player["deviation"]?.toInt(), it.getLeague.players[index].deviation)
                        assertEquals(player["rating"]?.toInt(), it.getLeague.players[index].rating)
                    }
                } ?: fail("Cannot get league by id $id")
            }
        }

        Then("I have first {int} games listed in league {string}") { first: Int, name: String ->
            runBlocking {
                delay(stepDelay)
                val id = dbUtil.leagueIdByName(name)
                val query = GetLeague(id, first)
                graphQlClient.execute(query).data?.let {
                    assertNotNull(it.getLeague.games)
                    assertFalse(it.getLeague.games.pageInfo.hasPreviousPage)
                    assertTrue(it.getLeague.games.pageInfo.hasNextPage)
                    assertEquals(first, it.getLeague.games.edges.size)
                    assertEquals(it.getLeague.games.pageInfo.startCursor, it.getLeague.games.edges.first().cursor)
                    assertEquals(it.getLeague.games.pageInfo.endCursor, it.getLeague.games.edges.last().cursor)
                    it.getLeague.games.edges.forEach { edge ->
                        assertEquals(it.getLeague.players.first().id, edge.node.playerOneId)
                        assertEquals(it.getLeague.players.last().id, edge.node.playerTwoId)
                    }
                    println(it)
                } ?: fail("Cannot get league by id $id")
            }
        }
    }
}