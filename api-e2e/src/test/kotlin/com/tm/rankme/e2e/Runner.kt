package com.tm.rankme.e2e

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import java.net.URL

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["src/test/resources/features"],
    plugin = ["pretty", "summary"]
)
class Runner {
    private val graphQlApiUrl = "http://localhost:9000/graphql"

    @Bean
    fun graphQlClient(): GraphQLKtorClient {
        return GraphQLKtorClient(URL(graphQlApiUrl))
    }
}