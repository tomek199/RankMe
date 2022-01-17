package com.tm.rankme.infrastructure.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
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
import kotlin.test.*

internal class MongoGameRepositoryTest {
    private val accessor: MongoGameAccessor = mockk()
    private val repository: GameRepository = MongoGameRepository(accessor)

    @Test
    internal fun `Should return game without result`() {
        // given
        val gameEntity = GameEntity(
            randomNanoId(), randomNanoId(), LocalDateTime.now(),
            randomNanoId(), "Batman", 1673, 286,
            randomNanoId(), "Superman", 2792, 173
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
            randomNanoId(), randomNanoId(), LocalDateTime.now(),
            randomNanoId(), "Batman", 1673, 286,
            randomNanoId(), "Superman", 2792, 173,
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
        val id = randomNanoId()
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
            randomNanoId(), randomNanoId(), LocalDateTime.now(),
            randomNanoId(), "Batman", 1673, 286,
            randomNanoId(), "Superman", 2792, 173
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
            randomNanoId(), randomNanoId(), LocalDateTime.now(),
            randomNanoId(), "Batman", 1673, 286,
            randomNanoId(), "Superman", 2792, 173,
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

    @Test
    internal fun `Should return empty games page for league`() {
        // given
        val leagueId = randomNanoId()
        every { accessor.getByLeagueIdOrderByTimestampDesc(leagueId, ofType(Pageable::class)) } returns
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
        val leagueId = randomNanoId()
        val games = List(7) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }

        every { accessor.getByLeagueIdOrderByTimestampDesc(leagueId, ofType(Pageable::class)) } returns
                PageImpl(games, PageRequest.of(0, 7), 10)
        // when
        val page = repository.byLeagueId(leagueId, 7)
        // then
        assertEquals(7, page.items.size)
        assertEquals(games.first().id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertFalse(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return middle games page for league`() {
        // given
        val leagueId = randomNanoId()
        val games = List(8) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(leagueId, games.first().timestamp, ofType(Pageable::class))
        } returns PageImpl(games.subList(1, 8), PageRequest.of(0, 7), 10)
        val afterCursor = Base64.getEncoder().encodeToString(games.first().timestamp.toString().toByteArray())
        // when
        val page = repository.byLeagueId(leagueId, 5, afterCursor)
        // then
        assertEquals(7, page.items.size)
        assertEquals(games[1].id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return last games page for league`() {
        // given
        val leagueId = randomNanoId()
        val games = List(7) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(leagueId, games.first().timestamp, ofType(Pageable::class))
        } returns PageImpl(games.takeLast(6), PageRequest.of(0, 6), 6)
        val afterCursor = Base64.getEncoder().encodeToString(games.first().timestamp.toString().toByteArray())
        // when
        val page = repository.byLeagueId(leagueId, 6, afterCursor)
        // then
        assertEquals(6, page.items.size)
        assertEquals(games[1].id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return empty completed games page for league`() {
        // given
        val leagueId = randomNanoId()
        every { accessor.getByLeagueIdAndResultNotNullOrderByTimestampDesc(leagueId, ofType(Pageable::class)) } returns
                PageImpl(emptyList(), PageRequest.of(0, 5), 0)
        // when
        val page = repository.completedByLeagueId(leagueId, 5)
        // then
        assertTrue(page.items.isEmpty())
        assertFalse(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return first completed games page for league`() {
        // given
        val leagueId = randomNanoId()
        val games = List(7) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        }

        every { accessor.getByLeagueIdAndResultNotNullOrderByTimestampDesc(leagueId, ofType(Pageable::class)) } returns
                PageImpl(games, PageRequest.of(0, 7), 10)
        // when
        val page = repository.completedByLeagueId(leagueId, 7)
        // then
        assertEquals(7, page.items.size)
        assertEquals(games.first().id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertFalse(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return middle completed games page for league`() {
        // given
        val leagueId = randomNanoId()
        val games = List(8) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        }
        every {
            accessor.getByLeagueIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(leagueId, games.first().timestamp, ofType(Pageable::class))
        } returns PageImpl(games.subList(1, 8), PageRequest.of(0, 7), 10)
        val afterCursor = Base64.getEncoder().encodeToString(games.first().timestamp.toString().toByteArray())
        // when
        val page = repository.completedByLeagueId(leagueId, 5, afterCursor)
        // then
        assertEquals(7, page.items.size)
        assertEquals(games[1].id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return last completed games page for league`() {
        // given
        val leagueId = randomNanoId()
        val games = List(7) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        }
        every {
            accessor.getByLeagueIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(leagueId, games.first().timestamp, ofType(Pageable::class))
        } returns PageImpl(games.takeLast(6), PageRequest.of(0, 6), 6)
        val afterCursor = Base64.getEncoder().encodeToString(games.first().timestamp.toString().toByteArray())
        // when
        val page = repository.completedByLeagueId(leagueId, 6, afterCursor)
        // then
        assertEquals(6, page.items.size)
        assertEquals(games[1].id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return empty scheduled games page for league`() {
        // given
        val leagueId = randomNanoId()
        every { accessor.getByLeagueIdAndResultNullOrderByTimestampDesc(leagueId, ofType(Pageable::class)) } returns
                PageImpl(emptyList(), PageRequest.of(0, 5), 0)
        // when
        val page = repository.scheduledByLeagueId(leagueId, 5)
        // then
        assertTrue(page.items.isEmpty())
        assertFalse(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return first scheduled games page for league`() {
        // given
        val leagueId = randomNanoId()
        val games = List(7) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }

        every { accessor.getByLeagueIdAndResultNullOrderByTimestampDesc(leagueId, ofType(Pageable::class)) } returns
                PageImpl(games, PageRequest.of(0, 7), 10)
        // when
        val page = repository.scheduledByLeagueId(leagueId, 7)
        // then
        assertEquals(7, page.items.size)
        assertEquals(games.first().id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertFalse(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    @Test
    internal fun `Should return middle scheduled games page for league`() {
        // given
        val leagueId = randomNanoId()
        val games = List(8) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByLeagueIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(leagueId, games.first().timestamp, ofType(Pageable::class))
        } returns PageImpl(games.subList(1, 8), PageRequest.of(0, 7), 10)
        val afterCursor = Base64.getEncoder().encodeToString(games.first().timestamp.toString().toByteArray())
        // when
        val page = repository.scheduledByLeagueId(leagueId, 5, afterCursor)
        // then
        assertEquals(7, page.items.size)
        assertEquals(games[1].id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    @Test
    internal fun `Should return last scheduled games page for league`() {
        // given
        val leagueId = randomNanoId()
        val games = List(7) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByLeagueIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(leagueId, games.first().timestamp, ofType(Pageable::class))
        } returns PageImpl(games.takeLast(6), PageRequest.of(0, 6), 6)
        val afterCursor = Base64.getEncoder().encodeToString(games.first().timestamp.toString().toByteArray())
        // when
        val page = repository.scheduledByLeagueId(leagueId, 6, afterCursor)
        // then
        assertEquals(6, page.items.size)
        assertEquals(games[1].id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    @Test
    internal fun `Should return empty games page for player`() {
        // given
        val playerId = randomNanoId()
        every { accessor.getByPlayerIdOrderByTimestampDesc(playerId, ofType(Pageable::class)) } returns
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
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val games = List(3) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }

        every { accessor.getByPlayerIdOrderByTimestampDesc(playerId, ofType(Pageable::class)) } returns
                PageImpl(games, PageRequest.of(0, 3), 8)
        // when
        val page = repository.byPlayerId(playerId, 3)
        // then
        assertEquals(3, page.items.size)
        assertEquals(games.first().id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertFalse(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return middle games page for player`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val games = List(6) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(
                playerId, games.first().timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games.subList(1, 6), PageRequest.of(0, 5), 8)
        val afterCursor = Base64.getEncoder().encodeToString(games.first().timestamp.toString().toByteArray())
        // when
        val page = repository.byPlayerId(playerId, 5, afterCursor)
        // then
        assertEquals(5, page.items.size)
        assertEquals(games[1].id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertTrue(page.hasNextPage)
    }

    @Test
    internal fun `Should return last games page for player`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val games = List(6) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        every {
            accessor.getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(
                playerId, games.first().timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games.takeLast(5), PageRequest.of(0, 5), 5)
        val afterCursor = Base64.getEncoder().encodeToString(games.first().timestamp.toString().toByteArray())
        // when
        val page = repository.byPlayerId(playerId, 5, afterCursor)
        // then
        assertEquals(5, page.items.size)
        assertEquals(games[1].id, page.items.first().node.id)
        assertEquals(games.last().id, page.items.last().node.id)
        assertTrue(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }
}