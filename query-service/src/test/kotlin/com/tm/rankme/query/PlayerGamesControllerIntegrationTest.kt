package com.tm.rankme.query

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.infrastructure.game.GameEntity
import com.tm.rankme.infrastructure.game.MongoGameAccessor
import com.tm.rankme.infrastructure.game.Result
import com.tm.rankme.model.game.PlayerGamesRepository
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

@WebMvcTest(controllers = [PlayerGamesController::class, PlayerGamesRepository::class])
internal class PlayerGamesControllerIntegrationTest {
    @Autowired
    private lateinit var mvc: MockMvc
    @MockkBean
    private lateinit var gameAccessor: MongoGameAccessor

    @Test
    internal fun `Should return page for games by player id after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                playerId, "Han Solo", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        )
        val timestamp = System.currentTimeMillis()
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities, PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/players/$playerId/games?first=2&after=$afterCursor")
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
    internal fun `Should return page for games by player id before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                playerId, "Han Solo", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        )
        val timestamp = System.currentTimeMillis()
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByPlayerIdAndTimestampGreaterThanOrderByTimestampAsc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities.reversed(), PageRequest.of(0, 2), 4)
        // when
        val result = mvc.get("/players/$playerId/games?first=2&before=$beforeCursor")
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
    internal fun `Should return empty page for games by player id`() {
        // given
        val playerId = randomNanoId()
        every {
            gameAccessor.getByPlayerIdOrderByTimestampDesc(playerId, ofType(Pageable::class))
        } returns PageImpl(emptyList(), PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/players/$playerId/games?first=3")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.hasPreviousPage") { value(false) }
            jsonPath("$.hasNextPage") { value(false) }
            jsonPath("$.items") { isEmpty() }
        }
    }

    @Test
    internal fun `Should return page for completed games by player id after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                playerId, "Han Solo", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        )
        val timestamp = System.currentTimeMillis()
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByPlayerIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities, PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/players/$playerId/completed-games?first=2&after=$afterCursor")
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
    internal fun `Should return page for completed games by player id before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                playerId, "Han Solo", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        )
        val timestamp = System.currentTimeMillis()
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByPlayerIdAndTimestampGreaterThanAndResultNotNullOrderByTimestampAsc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities.reversed(), PageRequest.of(0, 2), 3)
        // when
        val result = mvc.get("/players/$playerId/completed-games?first=2&before=$beforeCursor")
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
    internal fun `Should return empty page for completed games by player id`() {
        // given
        val playerId = randomNanoId()
        every {
            gameAccessor.getByPlayerIdAndResultNotNullOrderByTimestampDesc(playerId, ofType(Pageable::class))
        } returns PageImpl(emptyList(), PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/players/$playerId/completed-games?first=3")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.hasPreviousPage") { value(false) }
            jsonPath("$.hasNextPage") { value(false) }
            jsonPath("$.items") { isEmpty() }
        }
    }

    @Test
    internal fun `Should return page for scheduled games by player id after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                playerId, "Han Solo", Random.nextInt(), Random.nextInt()
            )
        )
        val timestamp = System.currentTimeMillis()
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByPlayerIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities, PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/players/$playerId/scheduled-games?first=2&after=$afterCursor")
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
    internal fun `Should return page for scheduled games by player id before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val entities = listOf(
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Batman", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                playerId, "Han Solo", Random.nextInt(), Random.nextInt()
            )
        )
        val timestamp = System.currentTimeMillis()
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        every {
            gameAccessor.getByPlayerIdAndTimestampGreaterThanAndResultNullOrderByTimestampAsc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities.reversed(), PageRequest.of(0, 2), 3)
        // when
        val result = mvc.get("/players/$playerId/scheduled-games?first=2&before=$beforeCursor")
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
    internal fun `Should return empty page for scheduled games by player id`() {
        // given
        val playerId = randomNanoId()
        every {
            gameAccessor.getByPlayerIdAndResultNullOrderByTimestampDesc(playerId, ofType(Pageable::class))
        } returns PageImpl(emptyList(), PageRequest.of(0, 2), 2)
        // when
        val result = mvc.get("/players/$playerId/scheduled-games?first=3")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.hasPreviousPage") { value(false) }
            jsonPath("$.hasNextPage") { value(false) }
            jsonPath("$.items") { isEmpty() }
        }
    }
}