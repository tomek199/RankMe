package com.tm.rankme.query

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.infrastructure.league.LeagueEntity
import com.tm.rankme.infrastructure.league.MongoLeagueAccessor
import com.tm.rankme.model.league.LeagueRepository
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
        val entity = LeagueEntity(randomNanoId(), "Star Wars", false, 3)
        every { leagueAccessor.findByIdOrNull(entity.id) } returns entity
        // when
        val result = mvc.get("/leagues/${entity.id}")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(entity.id) }
            jsonPath("$.name") { value(entity.name) }
            jsonPath("$.allowDraws") { value(entity.allowDraws) }
            jsonPath("$.maxScore") { value(entity.maxScore) }
        }
    }

    @Test
    internal fun `Should return empty response when league does not exist`() {
        // given
        val leagueId = randomNanoId()
        every { leagueAccessor.findByIdOrNull(leagueId) } returns null
        // when
        val result = mvc.get("/leagues/$leagueId")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$") { doesNotExist() }
        }
    }

    @Test
    internal fun `Should return leagues page after given cursor`() {
        // given
        val entities = listOf(
            LeagueEntity(randomNanoId(), "Star Wars", false, 3),
            LeagueEntity(randomNanoId(), "Marvel", true, 12),
        )
        val timestamp = System.currentTimeMillis()
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            leagueAccessor.getByTimestampGreaterThanOrderByTimestampAsc(timestamp, ofType(Pageable::class))
        } returns PageImpl(entities, PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/leagues?first=2&after=$afterCursor")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.hasPreviousPage") { value(true) }
            jsonPath("$.hasNextPage") { value(false) }
            jsonPath("$.items[0].node.id") { value(entities[0].id) }
            jsonPath("$.items[0].cursor") {
                Base64.getEncoder().encodeToString(entities[0].timestamp.toString().toByteArray())
            }
            jsonPath("$.items[1].node.id") { value(entities[1].id) }
            jsonPath("$.items[1].cursor") {
                Base64.getEncoder().encodeToString(entities[1].timestamp.toString().toByteArray())
            }
        }
    }

    @Test
    internal fun `Should return leagues page before given cursor`() {
        // given
        val entities = listOf(
            LeagueEntity(randomNanoId(), "Star Wars", false, 3),
            LeagueEntity(randomNanoId(), "Marvel", true, 12)
        )
        val timestamp = System.currentTimeMillis()
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            leagueAccessor.getByTimestampLessThanOrderByTimestampDesc(timestamp, ofType(Pageable::class))
        } returns PageImpl(entities.reversed(), PageRequest.of(0, 2), 3)
        // when
        val result = mvc.get("/leagues?first=2&before=$beforeCursor")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.hasPreviousPage") { value(true) }
            jsonPath("$.hasNextPage") { value(true) }
            jsonPath("$.items[0].node.id") { value(entities[0].id) }
            jsonPath("$.items[0].cursor") {
                Base64.getEncoder().encodeToString(entities[0].timestamp.toString().toByteArray())
            }
            jsonPath("$.items[1].node.id") { value(entities[1].id) }
            jsonPath("$.items[1].cursor") {
                Base64.getEncoder().encodeToString(entities[1].timestamp.toString().toByteArray())
            }
        }
    }

    @Test
    internal fun `Should return empty leagues page`() {
        // given
        every {
            leagueAccessor.getAllByOrderByTimestampAsc(ofType(Pageable::class))
        } returns PageImpl(emptyList(), PageRequest.of(0, 3), 0)
        // when
        val result = mvc.get("/leagues?first=3")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.hasPreviousPage") { value(false) }
            jsonPath("$.hasNextPage") { value(false) }
            jsonPath("$.items") { isEmpty() }
        }
    }
}