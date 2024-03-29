package com.tm.rankme.query

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.infrastructure.game.GameEntity
import com.tm.rankme.infrastructure.game.MongoGameAccessor
import com.tm.rankme.infrastructure.game.Result
import com.tm.rankme.model.game.GameRepository
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

@WebMvcTest(controllers = [LeagueGamesController::class, GameRepository::class])
internal class LeagueGamesControllerIntegrationTest {
    @Autowired
    private lateinit var mvc: MockMvc
    @MockkBean
    private lateinit var gameAccessor: MongoGameAccessor

    @Test
    internal fun `Should return games page by league id after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Han Solo", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        )
        val timestamp = System.currentTimeMillis()
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(
                leagueId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities, PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/leagues/$leagueId/games?first=2&after=$afterCursor")
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
            jsonPath("$.items[0].node.result") { isEmpty() }
            jsonPath("$.items[1].node.result") { isNotEmpty() }
        }
    }

    @Test
    internal fun `Should return games page by league id before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Han Solo", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        )
        val timestamp = System.currentTimeMillis()
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByLeagueIdAndTimestampGreaterThanOrderByTimestampAsc(
                leagueId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities.reversed(), PageRequest.of(0, 2), 3)
        // when
        val result = mvc.get("/leagues/$leagueId/games?first=2&before=$beforeCursor")
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
            jsonPath("$.items[0].node.result") { isEmpty() }
            jsonPath("$.items[1].node.result") { isNotEmpty() }
        }
    }

    @Test
    internal fun `Should return empty games page by league id`() {
        // given
        val leagueId = randomNanoId()
        every {
            gameAccessor.getByLeagueIdOrderByTimestampDesc(leagueId, ofType(Pageable::class))
        } returns PageImpl(emptyList(), PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/leagues/$leagueId/games?first=3")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.hasPreviousPage") { value(false) }
            jsonPath("$.hasNextPage") { value(false) }
            jsonPath("$.items") { isEmpty() }
        }
    }

    @Test
    internal fun `Should return completed games page by league id after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Han Solo", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        )
        val timestamp = System.currentTimeMillis()
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByLeagueIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(
                leagueId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities, PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/leagues/$leagueId/completed-games?first=2&after=$afterCursor")
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
            jsonPath("$.items[0].node.result") { isNotEmpty() }
            jsonPath("$.items[1].node.result") { isNotEmpty() }
        }
    }

    @Test
    internal fun `Should return completed games page by league id before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Han Solo", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        )
        val timestamp = System.currentTimeMillis()
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByLeagueIdAndTimestampGreaterThanAndResultNotNullOrderByTimestampAsc(
                leagueId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities.reversed(), PageRequest.of(0, 2), 3)
        // when
        val result = mvc.get("/leagues/$leagueId/completed-games?first=2&before=$beforeCursor")
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
            jsonPath("$.items[0].node.result") { isNotEmpty() }
            jsonPath("$.items[1].node.result") { isNotEmpty() }
        }
    }

    @Test
    internal fun `Should return empty completed games page by league id`() {
        // given
        val leagueId = randomNanoId()
        every {
            gameAccessor.getByLeagueIdAndResultNotNullOrderByTimestampDesc(leagueId, ofType(Pageable::class))
        } returns PageImpl(emptyList(), PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/leagues/$leagueId/completed-games?first=3")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.hasPreviousPage") { value(false) }
            jsonPath("$.hasNextPage") { value(false) }
            jsonPath("$.items") { isEmpty() }
        }
    }

    @Test
    internal fun `Should return scheduled games page by league id after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Han Solo", Random.nextInt(), Random.nextInt()
            )
        )
        val timestamp = System.currentTimeMillis()
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByLeagueIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(
                leagueId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities, PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/leagues/$leagueId/scheduled-games?first=2&after=$afterCursor")
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
            jsonPath("$.items[0].node.result") { isEmpty() }
            jsonPath("$.items[1].node.result") { isEmpty() }
        }
    }

    @Test
    internal fun `Should return scheduled games page by league id before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Han Solo", Random.nextInt(), Random.nextInt()
            )
        )
        val timestamp = System.currentTimeMillis()
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByLeagueIdAndTimestampGreaterThanAndResultNullOrderByTimestampAsc(
                leagueId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities.reversed(), PageRequest.of(0, 2), 3)
        // when
        val result = mvc.get("/leagues/$leagueId/scheduled-games?first=2&before=$beforeCursor")
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
            jsonPath("$.items[0].node.result") { isEmpty() }
            jsonPath("$.items[1].node.result") { isEmpty() }
        }
    }

    @Test
    internal fun `Should return empty scheduled games page by league id`() {
        // given
        val leagueId = randomNanoId()
        every {
            gameAccessor.getByLeagueIdAndResultNullOrderByTimestampDesc(leagueId, ofType(Pageable::class))
        } returns PageImpl(emptyList(), PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/leagues/$leagueId/scheduled-games?first=3")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.hasPreviousPage") { value(false) }
            jsonPath("$.hasNextPage") { value(false) }
            jsonPath("$.items") { isEmpty() }
        }
    }
}