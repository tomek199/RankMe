package com.tm.rankme.api.query

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.tm.rankme.storage.write.league.LeagueEventEmitter
import com.tm.rankme.storage.write.league.LeagueEventStorage
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class InfoQueryIntegrationTest {
    @MockBean
    private lateinit var eventStorage: LeagueEventStorage
    @MockBean
    private lateinit var eventEmitter: LeagueEventEmitter
    @Autowired
    private lateinit var template: GraphQLTestTemplate

    @Test
    internal fun `Should return info message`() {
        // given
        val request = "graphql/info.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals("RankMe GraphQL API 0.28-SNAPSHOT", response.get("$.data.info"))
    }
}
