package com.tm.rankme.query

import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.infrastructure.game.GameEntity
import com.tm.rankme.infrastructure.game.MongoGameAccessor
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

@WebMvcTest(controllers = [GameController::class, GameRepository::class])
internal class GameControllerIntegrationTest {
    @Autowired
    private lateinit var mvc: MockMvc
    @MockkBean
    private lateinit var gameAccessor: MongoGameAccessor

    @Test
    internal fun `Should return page for games by league id`() {
        // given
        val leagueId = UUID.randomUUID()
        val entities = listOf(
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                UUID.randomUUID().toString(), "Batman", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID().toString(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                UUID.randomUUID().toString(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID().toString(), "Han Solo", Random.nextInt(), Random.nextInt()
            )
        )
        every {
            gameAccessor.getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(
                leagueId.toString(), entities.first().timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities, PageRequest.of(0, 2), 2)
        val afterCursor = Base64.getEncoder().encodeToString(entities[0].timestamp.toString().toByteArray())
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
        }
    }

    @Test
    internal fun `Should return empty page for games by league id`() {
        // given
        val leagueId = UUID.randomUUID()
        every {
            gameAccessor.getByLeagueIdOrderByTimestampDesc(leagueId.toString(), ofType(Pageable::class))
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
    internal fun `Should return page for games by player id`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerId = UUID.randomUUID()
        val entities = listOf(
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                playerId.toString(), "Batman", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID().toString(), "Superman", Random.nextInt(), Random.nextInt()
            ),
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                UUID.randomUUID().toString(), "Darth Vader", Random.nextInt(), Random.nextInt(),
                playerId.toString(), "Han Solo", Random.nextInt(), Random.nextInt()
            )
        )
        every {
            gameAccessor.getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(
                playerId.toString(), entities.first().timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(entities, PageRequest.of(0, 2), 2)
        val afterCursor = Base64.getEncoder().encodeToString(entities[0].timestamp.toString().toByteArray())
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
        }
    }

    @Test
    internal fun `Should return empty page for games by player id`() {
        // given
        val playerId = UUID.randomUUID()
        every {
            gameAccessor.getByPlayerIdOrderByTimestampDesc(playerId.toString(), ofType(Pageable::class))
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
}