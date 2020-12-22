package com.tm.rankme.domain.player

import com.tm.rankme.domain.base.EventEmitter
import com.tm.rankme.domain.base.EventStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class PlayerRepositoryTest {
    private val eventStorage = mockk<EventStorage<Player>>(relaxed = true)
    private val eventEmitter = mockk<EventEmitter>(relaxed = true)
    private val repository = object : PlayerRepository(eventStorage, eventEmitter) {}

    @Test
    internal fun `Should get player by id`() {
        // given
        val aggregateId = UUID.randomUUID()
        val created = PlayerCreated(UUID.randomUUID(), "Optimus Prime", 238, 1854, aggregateId)
        every { eventStorage.events(aggregateId.toString()) } returns listOf(created)
        // when
        val player = repository.byId(aggregateId)
        // then
        assertEquals(aggregateId, player.id)
        assertEquals(0, player.version)
        assertTrue(player.pendingEvents.isEmpty())
        assertEquals(created.leagueId, player.leagueId)
        assertEquals(created.name, player.name)
        assertEquals(created.deviation, player.deviation)
        assertEquals(created.rating, player.rating)
    }

    @Test
    internal fun `Should store player`() {
        // given
        val player = Player.create(UUID.randomUUID(), "Optimus Prime")
        // when
        repository.store(player)
        // then
        verifySequence {
            eventStorage.save(ofType(PlayerCreated::class))
            eventEmitter.emit(ofType(PlayerCreated::class))
        }
    }
}