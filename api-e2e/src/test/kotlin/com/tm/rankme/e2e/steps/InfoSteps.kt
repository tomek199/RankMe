package com.tm.rankme.e2e.steps

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.tm.rankme.e2e.query.Version
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

class InfoSteps(
    private val graphQlClient: GraphQLKtorClient,
) : En {

    private var apiVersion: String? = null

    init {
        When("I send query to get API version") {
            runBlocking {
                val result = graphQlClient.execute(Version())
                apiVersion = result.data?.version
            }
        }

        Then("I receive API version {string}") { message: String ->
            assertEquals(apiVersion, message)
        }
    }
}