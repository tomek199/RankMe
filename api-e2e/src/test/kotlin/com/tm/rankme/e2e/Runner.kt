package com.tm.rankme.e2e

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.ClassRule
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.WaitStrategy
import java.io.File
import java.net.URL
import java.time.Duration

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

    companion object {
        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)

        @ClassRule
        @JvmField
        val docker = KDockerComposeContainer(File("../docker-compose.yml"))
            .withExposedService("api-service", 9000, waitStrategy())

        private fun waitStrategy(): WaitStrategy {
            return Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(5))
        }
    }
}