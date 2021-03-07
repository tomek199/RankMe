package com.tm.rankme.query

import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.infrastructure.LeagueEntity
import com.tm.rankme.infrastructure.MongoLeagueAccessor
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.*

@WebMvcTest(controllers = [LeagueController::class, LeagueRepository::class])
internal class LeagueControllerIntegrationTest {
    @Autowired
    private lateinit var mvc: MockMvc
    @MockkBean
    private lateinit var leagueAccessor: MongoLeagueAccessor

    @Test
    internal fun `Should return league`() {
        // given
        val entity = LeagueEntity(UUID.randomUUID(), "Star Wars", false, 3)
        every { leagueAccessor.findByIdOrNull(entity.id) } returns entity
        // when
        val result = mvc.get("/leagues/${entity.id}")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(entity.id.toString()) }
            jsonPath("$.name") { value(entity.name) }
            jsonPath("$.allowDraws") { value(entity.allowDraws) }
            jsonPath("$.maxScore") { value(entity.maxScore) }
        }
    }

    @Test
    internal fun `Should return empty response when league does not exist`() {
        // given
        val leagueId = UUID.randomUUID()
        every { leagueAccessor.findByIdOrNull(leagueId) } returns null
        // when
        val result = mvc.get("/leagues/$leagueId")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$") { doesNotExist() }
        }
    }
}