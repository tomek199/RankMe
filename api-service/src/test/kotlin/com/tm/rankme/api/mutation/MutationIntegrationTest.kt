package com.tm.rankme.api.mutation

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test"])
internal class MutationIntegrationTest {
    @Autowired
    private lateinit var template: GraphQLTestTemplate
    @MockkBean
    private lateinit var bus: CommandBus

    @BeforeEach
    internal fun setUp() {
        every { bus.execute(ofType(Command::class)) } just Runs
    }

    @TestFactory
    internal fun `Should handle mutations`() =
        // given
        listOf(
            "create-league",
            "rename-league",
            "change-league-setting",
            "create-player",
            "play-game",
            "schedule-game",
            "complete-game"
        ).map { request -> DynamicTest.dynamicTest("Given $request mutation should be executed") {
            // when
            val response = template.postForResource("graphql/${request}.graphql")
            // then
            verify(atMost = 7) { bus.execute(ofType(Command::class)) }
            assertTrue(response.isOk)
            assertTrue(response.rawResponse.toString().contains("SUBMITTED"))
        }
    }
}