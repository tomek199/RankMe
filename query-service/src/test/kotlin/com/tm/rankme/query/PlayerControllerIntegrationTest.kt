package com.tm.rankme.query

import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.infrastructure.player.MongoPlayerAccessor
import com.tm.rankme.infrastructure.player.PlayerEntity
import com.tm.rankme.model.player.PlayerRepository
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.*

@WebMvcTest(controllers = [PlayerController::class, PlayerRepository::class])
internal class PlayerControllerIntegrationTest {
    @Autowired
    private lateinit var mvc: MockMvc
    @MockkBean
    private lateinit var playerAccessor: MongoPlayerAccessor

    @Test
    internal fun `Should return player`() {
        // given
        val entity = PlayerEntity(UUID.randomUUID(), UUID.randomUUID(), "Optimus Prime", 318, 1653)
        every { playerAccessor.findByIdOrNull(entity.id) } returns entity
        // when
        val result = mvc.get("/players/${entity.id}")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(entity.id.toString()) }
            jsonPath("$.leagueId") { value(entity.leagueId.toString()) }
            jsonPath("$.name") { value(entity.name) }
            jsonPath("$.deviation") { value(entity.deviation) }
            jsonPath("$.rating") { value(entity.rating) }
        }
    }

    @Test
    internal fun `Should return empty response when player does not exist`() {
        // given
        val playerId = UUID.randomUUID()
        every { playerAccessor.findByIdOrNull(playerId) } returns null
        // when
        val result = mvc.get("/players/$playerId")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$") { doesNotExist() }
        }
    }

    @Test
    internal fun `Should return players list by league id`() {
        // given
        val leagueId = UUID.randomUUID()
        val entities = listOf(
            PlayerEntity(UUID.randomUUID(), leagueId, "Optimus Prime", 245, 1783),
            PlayerEntity(UUID.randomUUID(), leagueId, "Megatron", 184, 1298),
        )
        every { playerAccessor.findAllByLeagueId(leagueId) } returns entities
        // when
        val result = mvc.get("/leagues/$leagueId/players")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$[0].id") { value(entities[0].id.toString()) }
            jsonPath("$[0].leagueId") { value(entities[0].leagueId.toString()) }
            jsonPath("$[0].name") { value(entities[0].name) }
            jsonPath("$[0].deviation") { value(entities[0].deviation) }
            jsonPath("$[0].rating") { value(entities[0].rating) }
            jsonPath("$[1].id") { value(entities[1].id.toString()) }
            jsonPath("$[1].leagueId") { value(entities[1].leagueId.toString()) }
            jsonPath("$[1].name") { value(entities[1].name) }
            jsonPath("$[1].deviation") { value(entities[1].deviation) }
            jsonPath("$[1].rating") { value(entities[1].rating) }
        }
    }
    
    @Test
    internal fun `Should return empty list for players by league id`() {
        // given
        val leagueId = UUID.randomUUID()
        every { playerAccessor.findAllByLeagueId(leagueId) } returns emptyList()
        // when
        val result = mvc.get("/leagues/$leagueId/players")
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$") { isEmpty() }
        }
    }
}