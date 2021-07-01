package com.tm.rankme.infrastructure.game

import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class MongoGameRepositoryTest {
    private val accessor: MongoGameAccessor = mockk()
    private val repository: GameRepository = MongoGameRepository(accessor)

    @Test
    internal fun `Should return game without result`() {
        // given
        val gameEntity = GameEntity(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), LocalDateTime.now(),
            UUID.randomUUID().toString(), "Batman", 1673, 286,
            UUID.randomUUID().toString(), "Superman", 2792, 173
        )
        every { accessor.findByIdOrNull(gameEntity.id) } returns gameEntity
        // when
        val game = repository.byId(UUID.fromString(gameEntity.id))
        // then
        game?.let {
            assertEquals(gameEntity.id, it.id.toString())
            assertEquals(gameEntity.leagueId, it.leagueId.toString())
            assertEquals(gameEntity.dateTime, it.dateTime)
            assertEquals(gameEntity.playerOneId, it.playerOneId.toString())
            assertEquals(gameEntity.playerOneName, it.playerOneName)
            assertEquals(gameEntity.playerOneDeviation, it.playerOneDeviation)
            assertEquals(gameEntity.playerOneRating, it.playerOneRating)
            assertEquals(gameEntity.playerTwoId, it.playerTwoId.toString())
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
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), LocalDateTime.now(),
            UUID.randomUUID().toString(), "Batman", 1673, 286,
            UUID.randomUUID().toString(), "Superman", 2792, 173,
            result
        )
        every { accessor.findByIdOrNull(gameEntity.id) } returns gameEntity
        // when
        val game = repository.byId(UUID.fromString(gameEntity.id))
        // then
        game?.let {
            assertEquals(gameEntity.id, it.id.toString())
            assertEquals(gameEntity.leagueId, it.leagueId.toString())
            assertEquals(gameEntity.dateTime, it.dateTime)
            assertEquals(gameEntity.playerOneId, it.playerOneId.toString())
            assertEquals(gameEntity.playerOneName, it.playerOneName)
            assertEquals(gameEntity.playerOneDeviation, it.playerOneDeviation)
            assertEquals(gameEntity.playerOneRating, it.playerOneRating)
            assertEquals(gameEntity.playerTwoId, it.playerTwoId.toString())
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
        every { accessor.findByIdOrNull(id.toString()) } returns null
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
            assertEquals(game.id.toString(), it.id)
            assertEquals(game.leagueId.toString(), it.leagueId)
            assertEquals(game.dateTime, it.dateTime)
            assertEquals(game.playerOneId.toString(), it.playerOneId)
            assertEquals(game.playerOneName, it.playerOneName)
            assertEquals(game.playerOneDeviation, it.playerOneDeviation)
            assertEquals(game.playerOneRating, it.playerOneRating)
            assertEquals(game.playerTwoId.toString(), it.playerTwoId)
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
            assertEquals(game.id.toString(), it.id)
            assertEquals(game.leagueId.toString(), it.leagueId)
            assertEquals(game.dateTime, it.dateTime)
            assertEquals(game.playerOneId.toString(), it.playerOneId)
            assertEquals(game.playerOneName, it.playerOneName)
            assertEquals(game.playerOneDeviation, it.playerOneDeviation)
            assertEquals(game.playerOneRating, it.playerOneRating)
            assertEquals(game.playerTwoId.toString(), it.playerTwoId)
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

    @Test
    internal fun `Should return empty games page for league`() {
        // given
        val leagueId = UUID.randomUUID()
        every { accessor.getByLeagueIdOrderByTimestamp(leagueId.toString(), ofType(Pageable::class)) } returns
                PageImpl(emptyList(), PageRequest.of(0, 5), 0)
        // when
        val page = repository.byLeagueId(leagueId, 5)
        // then
        assertTrue(page.items.isEmpty())
        assertFalse(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return first games page for league`() {
        // given
        val leagueId = UUID.randomUUID()
        val games = List(14) {
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                UUID.randomUUID().toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID().toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }

        every { accessor.getByLeagueIdOrderByTimestamp(leagueId.toString(), ofType(Pageable::class)) } returns
                PageImpl(games.subList(0, 7), PageRequest.of(0, 7), 10)
        // when
        val page = repository.byLeagueId(leagueId, 7)
        // then
        assertEquals(7, page.items.size)
        assertEquals(games[0].id, page.items.first().node.id.toString())
        assertEquals(games[6].id, page.items.last().node.id.toString())
        assertFalse(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return middle games page for league`() {
        // given
        val leagueId = UUID.randomUUID()
        val games = List(14) {
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                UUID.randomUUID().toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID().toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByLeagueIdAndTimestampGreaterThanOrderByTimestamp(leagueId.toString(), games[3].timestamp, ofType(Pageable::class))
        } returns PageImpl(games.subList(4, 11), PageRequest.of(0, 7), 10)
        val afterCursor = Base64.getEncoder().encodeToString(games[3].timestamp.toString().toByteArray())
        // when
        val page = repository.byLeagueId(leagueId, 5, afterCursor)
        // then
        assertEquals(7, page.items.size)
        assertEquals(games[4].id, page.items.first().node.id.toString())
        assertEquals(games[10].id, page.items.last().node.id.toString())
        assertTrue(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return last games page for league`() {
        // given
        val leagueId = UUID.randomUUID()
        val games = List(14) {
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                UUID.randomUUID().toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID().toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByLeagueIdAndTimestampGreaterThanOrderByTimestamp(leagueId.toString(), games[7].timestamp, ofType(Pageable::class))
        } returns PageImpl(games.takeLast(6), PageRequest.of(0, 6), 6)
        val afterCursor = Base64.getEncoder().encodeToString(games[7].timestamp.toString().toByteArray())
        // when
        val page = repository.byLeagueId(leagueId, 6, afterCursor)
        // then
        assertEquals(6, page.items.size)
        assertEquals(games[8].id, page.items.first().node.id.toString())
        assertEquals(games[13].id, page.items.last().node.id.toString())
        assertTrue(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return empty games page for player`() {
        // given
        val playerId = UUID.randomUUID()
        every { accessor.getByPlayerIdOrderByTimestampDesc(playerId.toString(), ofType(Pageable::class)) } returns
                PageImpl(emptyList(), PageRequest.of(0, 3), 0)
        // when
        val page = repository.byPlayerId(playerId, 3)
        // then
        assertTrue(page.items.isEmpty())
        assertFalse(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return first games page for player`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerId = UUID.randomUUID()
        val games = List(10) {
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                playerId.toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID().toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }

        every { accessor.getByPlayerIdOrderByTimestampDesc(playerId.toString(), ofType(Pageable::class)) } returns
                PageImpl(games.subList(0, 3), PageRequest.of(0, 3), 8)
        // when
        val page = repository.byPlayerId(playerId, 3)
        // then
        assertEquals(3, page.items.size)
        assertEquals(games[0].id, page.items.first().node.id.toString())
        assertEquals(games[2].id, page.items.last().node.id.toString())
        assertFalse(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return middle games page for player`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerId = UUID.randomUUID()
        val games = List(10) {
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                UUID.randomUUID().toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                playerId.toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByPlayerIdAndCursorOrderByTimestampDesc(
                playerId.toString(), games[1].timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games.subList(2, 7), PageRequest.of(0, 5), 8)
        val afterCursor = Base64.getEncoder().encodeToString(games[2].timestamp.toString().toByteArray())
        // when
        val page = repository.byPlayerId(playerId, 5, afterCursor)
        // then
        assertEquals(5, page.items.size)
        assertEquals(games[2].id, page.items.first().node.id.toString())
        assertEquals(games[6].id, page.items.last().node.id.toString())
        assertTrue(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return last games page for player`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerId = UUID.randomUUID()
        val games = List(10) {
            GameEntity(
                UUID.randomUUID().toString(), leagueId.toString(), LocalDateTime.now(),
                playerId.toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                UUID.randomUUID().toString(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByPlayerIdAndCursorOrderByTimestampDesc(
                playerId.toString(), games[4].timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games.takeLast(5), PageRequest.of(0, 5), 5)
        val afterCursor = Base64.getEncoder().encodeToString(games[4].timestamp.toString().toByteArray())
        // when
        val page = repository.byPlayerId(playerId, 5, afterCursor)
        // then
        assertEquals(5, page.items.size)
        assertEquals(games[5].id, page.items.first().node.id.toString())
        assertEquals(games[9].id, page.items.last().node.id.toString())
        assertTrue(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }
}