package com.tm.rankme.api.query

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.storage.read.league.LeagueEntity
import com.tm.rankme.storage.read.league.MongoLeagueAccessor
import io.mockk.every
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class QueryIntegrationTest {
    @MockkBean
    private lateinit var leagueAccessor: MongoLeagueAccessor
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

    @Test
    internal fun `Should execute 'get league' query and return league`() {
        // given
        val id = UUID.fromString("83222ad3-219c-48b0-bcc2-817efb61cfda")
        val leagueEntity = LeagueEntity(id, "Star Wars", true, 5)
        every { leagueAccessor.findByIdOrNull(id) } returns leagueEntity
        val request = "graphql/get-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertEquals(id.toString(), response.get("$.data.getLeague.id"))
        assertEquals(leagueEntity.name, response.get("$.data.getLeague.name"))
        assertEquals(leagueEntity.maxScore, response.get("$.data.getLeague.maxScore", Int::class.java))
        assertEquals(leagueEntity.allowDraws, response.get("$.data.getLeague.allowDraws", Boolean::class.java))
    }

    @Test
    internal fun `Should execute 'get league' query and return null`() {
        // given
        val id = UUID.fromString("83222ad3-219c-48b0-bcc2-817efb61cfda")
        every { leagueAccessor.findByIdOrNull(id) } returns null
        val request = "graphql/get-league.graphql"
        // when
        val response = template.postForResource(request)
        // then
        assertTrue(response.isOk)
        assertNull(response.get("$.data.getLeague"))
    }
}
