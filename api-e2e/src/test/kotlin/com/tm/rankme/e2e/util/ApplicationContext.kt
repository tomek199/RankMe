package com.tm.rankme.e2e.util

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.query.GetLeague
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ApplicationContext(
    private val graphQlClient: GraphQLKtorClient,
    @Value("\${cucumber.step-delay}") private val stepDelay: Long
) {
    var leagueId: String? = null
        set(value) {
            field = value
            update()
        }
    private val players: MutableMap<String, String> = mutableMapOf()

    fun update() {
        runBlocking {
            val id = leagueId ?: throw IllegalStateException("League id is not initialized")
            graphQlClient.execute(GetLeague(id)).data
                ?.let { it.league.players?.forEach { player -> players[player.name] = player.id } }
        }
    }

    fun leagueId() = leagueId ?: throw IllegalStateException("League id is not initialized")
    fun playerId(name: String): String = players[name] ?: throw IllegalStateException("Player $name is not found")
}