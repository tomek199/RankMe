package com.tm.rankme.e2e

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.ClassRule
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.WaitStrategy
import java.io.File
import java.net.URL
import java.time.Duration

@RunWith(Cucumber::class)
@PropertySource("classpath:cucumber.properties")
@CucumberOptions(
    features = ["src/test/resources/features"],
    plugin = ["pretty", "summary"]
)
class Runner(
    @Value("\${api-service.url}") private val apiServiceUrl: String
) {

    @Bean
    fun graphQlClient(): GraphQLKtorClient {
        return GraphQLKtorClient(URL(apiServiceUrl))
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