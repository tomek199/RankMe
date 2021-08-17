package com.tm.rankme.infrastructure.player

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.player.Player
import com.tm.rankme.domain.player.PlayerCreated
import com.tm.rankme.domain.player.PlayerPlayedGame
import com.tm.rankme.infrastructure.InfrastructureException
import io.mockk.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class PostgresPlayerRepositoryTest {
    private val accessor = mockk<PlayerAccessor>()
    private val mapper = mockk<PlayerMapper>()
    private val repository = PostgresPlayerRepository(accessor, mapper)

    @Test
    internal fun `Should save 'player-created' event with initial version 0`() {
        // given
        val player = Player.create(randomNanoId(), "Optimus Prime")
        every { mapper.serialize(ofType(PlayerCreated::class)) } returns String()
        every { accessor.save(ofType(PlayerEntity::class)) } returns mockk()
        // when
        repository.store(player)
        // then
        val entitySlot = slot<PlayerEntity>()
        verify(exactly = 0) { accessor.getFirstByAggregateIdOrderByTimestampDesc(player.id) }
        verifySequence {
            mapper.serialize(ofType(PlayerCreated::class))
            accessor.save(capture(entitySlot))
        }
        entitySlot.captured.let {
            assertEquals(player.id, it.aggregateId)
            assertEquals("player-created", it.type)
            assertEquals(0, it.version)
            assertNotNull(it.payload)
        }
    }

    @Test
    internal fun `Should save 'player-played-game' event with version 1`() {
        // given
        val leagueId = randomNanoId()
        val player = Player.from(listOf(PlayerCreated(leagueId, "Batman")))
        val opponent = Player.from(listOf(PlayerCreated(leagueId, "Superman")))
        player.playedWith(opponent, 3, 4)
        every { mapper.serialize(ofType(PlayerPlayedGame::class)) } returns String()
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(player.id) } returns
            PlayerEntity(player.id, "player-played-game", 0, 12345, "{}", 1)
        every { accessor.save(ofType(PlayerEntity::class)) } returns mockk()
        // when
        repository.store(player)
        // then
        val entitySlot = slot<PlayerEntity>()
        verifySequence {
            accessor.getFirstByAggregateIdOrderByTimestampDesc(player.id)
            mapper.serialize(ofType(PlayerPlayedGame::class))
            accessor.save(capture(entitySlot))
        }
        entitySlot.captured.let {
            assertEquals(player.id, it.aggregateId)
            assertEquals("player-played-game", it.type)
            assertEquals(1, it.version)
            assertNotNull(it.payload)
        }
    }

    @Test
    internal fun `Should throw exception when cannot get actual aggregate version`() {
        // given
        val leagueId = randomNanoId()
        val player = Player.from(listOf(PlayerCreated(leagueId, "Batman")))
        val opponent = Player.from(listOf(PlayerCreated(leagueId, "Superman")))
        player.playedWith(opponent, 3, 4)
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(player.id) } returns null
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.store(player) }
        // then
        assertEquals("Cannon get actual version of aggregate id=${player.id}", exception.message)
    }

    @Test
    internal fun `Should throw exception when event version is out of date`() {
        // given
        val leagueId = randomNanoId()
        val player = Player.from(listOf(PlayerCreated(leagueId, "Batman")))
        val opponent = Player.from(listOf(PlayerCreated(leagueId, "Superman")))
        player.playedWith(opponent, 3, 4)
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(player.id) } returns
            PlayerEntity(player.id, "player-played-game", 1, 12345, "{}", 2)
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.store(player) }
        // then
        assertEquals("Version mismatch of aggregate id=${player.id}", exception.message)
    }

    @Test
    internal fun `Should return aggregate from events`() {
        // given
        val aggregateId = randomNanoId()
        val leagueId = randomNanoId()
        every { accessor.getByAggregateIdOrderByTimestampAsc(aggregateId) } returns listOf(
            PlayerEntity(aggregateId, "player-created", 0, 11111, "{}", 1),
            PlayerEntity(aggregateId, "player-played-game", 1, 22222, "{}", 2),
        )
        every { mapper.deserialize(ofType(String::class), ofType(String::class)) } returnsMany listOf(
            PlayerCreated(leagueId, "Optimus Prime", aggregateId = aggregateId),
            PlayerPlayedGame(-45, -150, 3, aggregateId, 1)
        )
        // when
        val player = repository.byId(aggregateId)
        // then
        assertEquals(aggregateId, player.id)
        assertEquals(1, player.version)
        assertTrue(player.pendingEvents.isEmpty())
        assertEquals("Optimus Prime", player.name)
        assertNotNull(player.lastGame)
        assertEquals(305, player.deviation)
        assertEquals(1350, player.rating)
    }

    @Test
    internal fun `Should throw exception when events for aggregate are not found`() {
        // given
        val aggregateId = randomNanoId()
        every { accessor.getByAggregateIdOrderByTimestampAsc(aggregateId) } returns emptyList()
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.byId(aggregateId) }
        // then
        assertEquals("Stream $aggregateId is not found", exception.message)
    }

    @Test
    internal fun `Should create entity`() {
        // given
        val aggregateId = randomNanoId()
        val type = "event-type"
        val version: Long = 5
        val timestamp: Long = 12345
        val payload = "{}"
        val id: Long = 6
        // when
        val entity = PlayerEntity(aggregateId, type, version, timestamp, payload, id)
        // then
        assertEquals(aggregateId, entity.aggregateId)
        assertEquals(type, entity.type)
        assertEquals(version, entity.version)
        assertEquals(timestamp, entity.timestamp)
        assertEquals(payload, entity.payload)
        assertEquals(id, entity.id)
    }
}