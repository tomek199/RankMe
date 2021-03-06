package com.tm.rankme.query

import com.ninjasquad.springmockk.MockkBean
import com.tm.rankme.infrastructure.MongoPlayerAccessor
import com.tm.rankme.infrastructure.PlayerEntity
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
}