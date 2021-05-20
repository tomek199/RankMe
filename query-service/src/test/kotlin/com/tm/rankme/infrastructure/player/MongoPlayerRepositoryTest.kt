package com.tm.rankme.infrastructure.player

import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class MongoPlayerRepositoryTest {
    private val accessor: MongoPlayerAccessor = mockk()
    private val repository: PlayerRepository = MongoPlayerRepository(accessor)

    @Test
    internal fun `Should return player`() {
        // given
        val playerEntity = PlayerEntity(UUID.randomUUID(), UUID.randomUUID(), "Optimus Prime", 197, 2103)
        every { accessor.findByIdOrNull(playerEntity.id) } returns playerEntity
        // when
        val player = repository.byId(playerEntity.id)
        // then
        assertTrue(player != null)
        assertEquals(playerEntity.id, player.id)
        assertEquals(playerEntity.leagueId, player.leagueId)
        assertEquals(playerEntity.name, player.name)
        assertEquals(playerEntity.deviation, player.deviation)
        assertEquals(playerEntity.rating, player.rating)
    }

    @Test
    internal fun `Should return null when player does not exist`() {
        // given
        val id = UUID.randomUUID()
        every { accessor.findByIdOrNull(id) } returns null
        // when
        val player = repository.byId(id)
        // then
        assertNull(player)
    }

    @Test
    internal fun `Should store player`() {
        // given
        every { accessor.save(ofType(PlayerEntity::class)) } returns mockk()
        val player = Player(UUID.randomUUID(), UUID.randomUUID(), "Optimus Prime", 185, 2407)
        // when
        repository.store(player)
        // then
        val entitySlot = slot<PlayerEntity>()
        verify(exactly = 1) { accessor.save(capture(entitySlot)) }
        entitySlot.captured.let {
            assertEquals(player.id, it.id)
            assertEquals(player.leagueId, it.leagueId)
            assertEquals(player.name, it.name)
            assertEquals(player.deviation, it.deviation)
            assertEquals(player.rating, it.rating)
        }
    }

    @Test
    internal fun `Should return players list by league id`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerEntities = listOf(
            PlayerEntity(UUID.randomUUID(), leagueId, "Optimus Prime", 245, 1783),
            PlayerEntity(UUID.randomUUID(), leagueId, "Megatron", 184, 1298),
            PlayerEntity(UUID.randomUUID(), leagueId, "Bumblebee", 326, 2864)
        )
        every { accessor.findAllByLeagueId(leagueId) } returns playerEntities
        // when
        val players = repository.byLeagueId(leagueId)
        // then
        players.forEachIndexed { index, player ->
            assertEquals(leagueId, player.leagueId)
            assertEquals(playerEntities[index].id, player.id)
            assertEquals(playerEntities[index].name, player.name)
            assertEquals(playerEntities[index].deviation, player.deviation)
            assertEquals(playerEntities[index].rating, player.rating)
        }
    }

    @Test
    internal fun `Should return empty list for players by league id`() {
        // given
        val leagueId = UUID.randomUUID()
        every { accessor.findAllByLeagueId(leagueId) } returns emptyList()
        // when
        val players = repository.byLeagueId(leagueId)
        // then
        assertTrue(players.isEmpty())
    }
}