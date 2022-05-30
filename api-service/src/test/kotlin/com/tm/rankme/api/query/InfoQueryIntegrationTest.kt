package com.tm.rankme.api.query

import com.graphql.spring.boot.test.GraphQLTestTemplate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test"])
internal class InfoQueryIntegrationTest {
    @Autowired
    private lateinit var buildProperties: BuildProperties
    @Autowired
    private lateinit var template: GraphQLTestTemplate

    @Test
    internal fun `Should return api version`() {
        // given
        val request = "graphql/query/version.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(buildProperties.version, response.get("$.data.version"))
    }
}