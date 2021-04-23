package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.query.InfoQuery
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

class InfoSteps(
    private val graphQlClient: GraphQLKtorClient,
) : En {

    private var infoMessage: String? = null

    init {
        When("I send query to get info message") {
            runBlocking {
                val result = graphQlClient.execute(InfoQuery())
                infoMessage = result.data?.info
            }
        }

        Then("I receive info message {string}") { message: String ->
            assertEquals(infoMessage, message)
        }
    }
}