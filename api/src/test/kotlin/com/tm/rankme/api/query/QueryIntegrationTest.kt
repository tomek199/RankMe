package com.tm.rankme.api.query

import com.graphql.spring.boot.test.GraphQLTestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class QueryIntegrationTest {
    @Autowired
    private lateinit var buildProperties: BuildProperties
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
        assertEquals("RankMe GraphQL API ${buildProperties.version}", response.get("$.data.info"))
    }
}