package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking

class LeagueSteps(private val graphQlClient: GraphQLKtorClient) : En {
    init {
        Given("I create league {string}") { name: String ->
            runBlocking {
//                TODO implement this
//                val result = graphQlClient.execute(InfoQuery())/
//                println(result.data?.info)
            }
        }
    }
}