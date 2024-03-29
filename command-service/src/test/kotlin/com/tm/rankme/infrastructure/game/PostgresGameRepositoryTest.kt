package com.tm.rankme.infrastructure.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GamePlayed
import com.tm.rankme.domain.game.GameScheduled
import com.tm.rankme.domain.game.Result
import com.tm.rankme.infrastructure.InfrastructureException
import io.mockk.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class PostgresGameRepositoryTest {
    private val accessor = mockk<GameAccessor>()
    private val mapper = mockk<GameMapper>()
    private val repository = PostgresGameRepository(accessor, mapper)

    @Test
    internal fun `Should save 'game-scheduled' event with initial version 0`() {
        // given
        val game = Game.scheduled(randomNanoId(), LocalDateTime.now(), randomNanoId(), randomNanoId())
        every { mapper.serialize(ofType(GameScheduled::class)) } returns String()
        every { accessor.save(ofType(GameEntity::class)) } returns mockk()
        // when
        repository.store(game)
        // then
        val entitySlot = slot<GameEntity>()
        verify(exactly = 0) { accessor.getFirstByAggregateIdOrderByTimestampDesc(game.id) }
        verifySequence {
            mapper.serialize(ofType(GameScheduled::class))
            accessor.save(capture(entitySlot))
        }
        entitySlot.captured.let {
            assertEquals(game.id, it.aggregateId)
            assertEquals("game-scheduled", it.type)
            assertEquals(0, it.version)
            assertNotNull(it.payload)
        }
    }

    @Test
    internal fun `Should save 'game-played' event with version 1`() {
        // given
        val game = Game.from(listOf(GameScheduled(randomNanoId(), randomNanoId(), randomNanoId(), 12345)))
        game.complete(
            Result(0, 184, -34, 2443, -176),
            Result(1, 194, -42, 2159, 148)
        )
        every { mapper.serialize(ofType(GamePlayed::class)) } returns String()
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(game.id) } returns
            GameEntity(game.id, "game-played", 0, 12345, "{}", 1)
        every { accessor.save(ofType(GameEntity::class)) } returns mockk()
        // when
        repository.store(game)
        // then
        val entitySlot = slot<GameEntity>()
        verifySequence {
            accessor.getFirstByAggregateIdOrderByTimestampDesc(game.id)
            mapper.serialize(ofType(GamePlayed::class))
            accessor.save(capture(entitySlot))
        }
        entitySlot.captured.let {
            assertEquals(game.id, it.aggregateId)
            assertEquals("game-played", it.type)
            assertEquals(1, it.version)
            assertNotNull(it.payload)
        }
    }

    @Test
    internal fun `Should throw exception when cannot get actual aggregate version`() {
        // given
        val game = Game.from(listOf(GameScheduled(randomNanoId(), randomNanoId(), randomNanoId(), 12345)))
        game.complete(
            Result(0, 184, -34, 2443, -176),
            Result(1, 194, -42, 2159, 148)
        )
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(game.id) } returns null
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.store(game) }
        // then
        assertEquals("Cannon get actual version of aggregate id=${game.id}", exception.message)
    }

    @Test
    internal fun `Should throw exception when event version is out of date`() {
        // given
        val game = Game.from(listOf(GameScheduled(randomNanoId(), randomNanoId(), randomNanoId(), 12345)))
        game.complete(
            Result(0, 184, -34, 2443, -176),
            Result(1, 194, -42, 2159, 148)
        )
        every { accessor.getFirstByAggregateIdOrderByTimestampDesc(game.id) } returns
            GameEntity(game.id, "game-played", 1, 12345, "{}", 2)
        // when
        val exception = assertFailsWith<InfrastructureException> { repository.store(game) }
        // then
        assertEquals("Version mismatch of aggregate id=${game.id}", exception.message)
    }

    @Test
    internal fun `Should return aggregate from events`() {
        // given
        val aggregateId = randomNanoId()
        val leagueId = randomNanoId()
        val playerOneId = randomNanoId()
        val playerTwoId = randomNanoId()
        every { accessor.getByAggregateIdOrderByTimestampAsc(aggregateId) } returns listOf(
            GameEntity(aggregateId, "game-scheduled", 0, 11111, "{}", 1),
            GameEntity(aggregateId, "game-played", 1, 22222, "{}", 2),
        )
        every { mapper.deserialize(ofType(String::class), ofType(String::class)) } returnsMany listOf(
            GameScheduled(leagueId, playerOneId, playerTwoId, 11111, aggregateId),
            GamePlayed(leagueId,
                playerOneId, 4, 189, -45, 2724, 75,
                playerTwoId, 3, 217, -35, 1498, -75,
                22222, aggregateId, 1)
        )
        // when
        val game = repository.byId(aggregateId)
        // then
        assertEquals(aggregateId, game.id)
        assertEquals(1, game.version)
        assertTrue(game.pendingEvents.isEmpty())
        assertEquals(Pair(playerOneId, playerTwoId), game.playerIds)
        assertEquals(Result(4, 189, -45, 2724, 75), game.result!!.first)
        assertEquals(Result(3, 217, -35, 1498, -75), game.result!!.second)
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
        val entity = GameEntity(aggregateId, type, version, timestamp, payload, id)
        // then
        assertEquals(aggregateId, entity.aggregateId)
        assertEquals(type, entity.type)
        assertEquals(version, entity.version)
        assertEquals(timestamp, entity.timestamp)
        assertEquals(payload, entity.payload)
        assertEquals(id, entity.id)
    }
}