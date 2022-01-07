package com.tm.rankme.e2e

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import java.net.URL

@RunWith(Cucumber::class)
@PropertySource("classpath:cucumber.properties")
@CucumberOptions(
    features = ["src/test/resources/features"],
    plugin = ["pretty", "summary"],
    tags = "not @Ignore"
)
class Runner(
    @Value("\${api-service.url}") private val apiServiceUrl: String
) {

    @Bean
    fun graphQlClient(): GraphQLKtorClient {
        return GraphQLKtorClient(URL(apiServiceUrl))
    }

    /* Configuration that might be used to run tests against environment established in docker-compose.
    companion object {
        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)

        @ClassRule
        @JvmField
        val docker = KDockerComposeContainer(File("../docker-compose.yml"))
            .withExposedService("api-service", 9040, waitStrategy())

        private fun waitStrategy(): WaitStrategy {
            return Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(5))
        }
    }*/
}