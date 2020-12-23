package com.tm.rankme.storage.read.player

import com.tm.rankme.model.player.PlayerRepository
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue   
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

internal class MongoPlayerRepositoryTest {
    private val playerAccessor: MongoPlayerAccessor = mockk()
    private val repository: PlayerRepository = MongoPlayerRepository(playerAccessor)

    @Test
    internal fun `Should return player`() {
        // given
        val playerEntity = PlayerEntity(UUID.randomUUID(), UUID.randomUUID(), "Optimus Prime", 197, 2103)
        every { playerAccessor.findByIdOrNull(playerEntity.id) } returns playerEntity
        // when
        val player = repository.byId(playerEntity.id)
        // then
        assertTrue(player != null)
        assertEquals(playerEntity.id, player.id)
        assertEquals(playerEntity.name, player.name)
        assertEquals(playerEntity.deviation, player.deviation)
        assertEquals(playerEntity.rating, player.rating)
    }

    @Test
    internal fun `Should return null when player does not exist`() {
        // given
        val id = UUID.randomUUID()
        every { playerAccessor.findByIdOrNull(id) } returns null
        // when
        val player = repository.byId(id)
        // then
        assertNull(player)
    }
}