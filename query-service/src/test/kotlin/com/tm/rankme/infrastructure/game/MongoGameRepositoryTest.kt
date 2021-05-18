package com.tm.rankme.infrastructure.game

import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class MongoGameRepositoryTest {
    private val accessor: MongoGameAccessor = mockk()
    private val repository: GameRepository = MongoGameRepository(accessor)

    @Test
    internal fun `Should return game without result`() {
        // given
        val gameEntity = GameEntity(
            UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(),
            UUID.randomUUID(), "Batman", 1673, 286,
            UUID.randomUUID(), "Superman", 2792, 173
        )
        every { accessor.findByIdOrNull(gameEntity.id) } returns gameEntity
        // when
        val game = repository.byId(gameEntity.id)
        // then
        game?.let {
            assertEquals(gameEntity.id, it.id)
            assertEquals(gameEntity.leagueId, it.leagueId)
            assertEquals(gameEntity.dateTime, it.dateTime)
            assertEquals(gameEntity.playerOneId, it.playerOneId)
            assertEquals(gameEntity.playerOneName, it.playerOneName)
            assertEquals(gameEntity.playerOneDeviation, it.playerOneDeviation)
            assertEquals(gameEntity.playerOneRating, it.playerOneRating)
            assertEquals(gameEntity.playerTwoId, it.playerTwoId)
            assertEquals(gameEntity.playerTwoName, it.playerTwoName)
            assertEquals(gameEntity.playerTwoDeviation, it.playerTwoDeviation)
            assertEquals(gameEntity.playerTwoRating, it.playerTwoRating)
            assertNull(it.result)
        } ?: fail("Game is null")
    }

    @Test
    internal fun `Should return game with result`() {
        // given
        val result = Result(2, -34, 65, 1, -45, -57)
        val gameEntity = GameEntity(
            UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(),
            UUID.randomUUID(), "Batman", 1673, 286,
            UUID.randomUUID(), "Superman", 2792, 173,
            result
        )
        every { accessor.findByIdOrNull(gameEntity.id) } returns gameEntity
        // when
        val game = repository.byId(gameEntity.id)
        // then
        game?.let {
            assertEquals(gameEntity.id, it.id)
            assertEquals(gameEntity.leagueId, it.leagueId)
            assertEquals(gameEntity.dateTime, it.dateTime)
            assertEquals(gameEntity.playerOneId, it.playerOneId)
            assertEquals(gameEntity.playerOneName, it.playerOneName)
            assertEquals(gameEntity.playerOneDeviation, it.playerOneDeviation)
            assertEquals(gameEntity.playerOneRating, it.playerOneRating)
            assertEquals(gameEntity.playerTwoId, it.playerTwoId)
            assertEquals(gameEntity.playerTwoName, it.playerTwoName)
            assertEquals(gameEntity.playerTwoDeviation, it.playerTwoDeviation)
            assertEquals(gameEntity.playerTwoRating, it.playerTwoRating)
            assertEquals(gameEntity.result!!.playerOneScore, it.result!!.playerOneScore)
            assertEquals(gameEntity.result!!.playerOneDeviationDelta, it.result!!.playerOneDeviationDelta)
            assertEquals(gameEntity.result!!.playerOneRatingDelta, it.result!!.playerOneRatingDelta)
            assertEquals(gameEntity.result!!.playerTwoScore, it.result!!.playerTwoScore)
            assertEquals(gameEntity.result!!.playerTwoDeviationDelta, it.result!!.playerTwoDeviationDelta)
            assertEquals(gameEntity.result!!.playerTwoRatingDelta, it.result!!.playerTwoRatingDelta)
        } ?: fail("Game is null")
    }

    @Test
    internal fun `Should return null when game does not exist`() {
        // given
        val id = UUID.randomUUID()
        every { accessor.findByIdOrNull(id) } returns null
        // when
        val game = repository.byId(id)
        // then
        assertNull(game)
    }

    @Test
    internal fun `Should store game without result`() {
        // given
        every { accessor.save(ofType(GameEntity::class)) } returns mockk()
        val game = Game(
            UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(),
            UUID.randomUUID(), "Batman", 1673, 286,
            UUID.randomUUID(), "Superman", 2792, 173
        )
        // when
        repository.store(game)
        // then
        val entitySlot = slot<GameEntity>()
        verify(exactly = 1) { accessor.save(capture(entitySlot)) }
        entitySlot.captured.let {
            assertEquals(game.id, it.id)
            assertEquals(game.leagueId, it.leagueId)
            assertEquals(game.dateTime, it.dateTime)
            assertEquals(game.playerOneId, it.playerOneId)
            assertEquals(game.playerOneName, it.playerOneName)
            assertEquals(game.playerOneDeviation, it.playerOneDeviation)
            assertEquals(game.playerOneRating, it.playerOneRating)
            assertEquals(game.playerTwoId, it.playerTwoId)
            assertEquals(game.playerTwoName, it.playerTwoName)
            assertEquals(game.playerTwoDeviation, it.playerTwoDeviation)
            assertEquals(game.playerTwoRating, it.playerTwoRating)
            assertNull(game.result)
        }
    }

    @Test
    internal fun `Should store game with result`() {
        // given
        every { accessor.save(ofType(GameEntity::class)) } returns mockk()
        val result = com.tm.rankme.model.game.Result(2, -34, 65, 1, -45, -57)
        val game = Game(
            UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(),
            UUID.randomUUID(), "Batman", 1673, 286,
            UUID.randomUUID(), "Superman", 2792, 173,
            result
        )
        // when
        repository.store(game)
        // then
        val entitySlot = slot<GameEntity>()
        verify(exactly = 1) { accessor.save(capture(entitySlot)) }
        entitySlot.captured.let {
            assertEquals(game.id, it.id)
            assertEquals(game.leagueId, it.leagueId)
            assertEquals(game.dateTime, it.dateTime)
            assertEquals(game.playerOneId, it.playerOneId)
            assertEquals(game.playerOneName, it.playerOneName)
            assertEquals(game.playerOneDeviation, it.playerOneDeviation)
            assertEquals(game.playerOneRating, it.playerOneRating)
            assertEquals(game.playerTwoId, it.playerTwoId)
            assertEquals(game.playerTwoName, it.playerTwoName)
            assertEquals(game.playerTwoDeviation, it.playerTwoDeviation)
            assertEquals(game.playerTwoRating, it.playerTwoRating)
            assertEquals(game.result!!.playerOneScore, it.result!!.playerOneScore)
            assertEquals(game.result!!.playerOneDeviationDelta, it.result!!.playerOneDeviationDelta)
            assertEquals(game.result!!.playerOneRatingDelta, it.result!!.playerOneRatingDelta)
            assertEquals(game.result!!.playerTwoScore, it.result!!.playerTwoScore)
            assertEquals(game.result!!.playerTwoDeviationDelta, it.result!!.playerTwoDeviationDelta)
            assertEquals(game.result!!.playerTwoRatingDelta, it.result!!.playerTwoRatingDelta)
        }
    }
}