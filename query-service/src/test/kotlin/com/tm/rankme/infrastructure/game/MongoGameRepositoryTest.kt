package com.tm.rankme.infrastructure.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.model.Page
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
import java.lang.System.currentTimeMillis
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
        assertGamesPage(games, false, true, page)
    }

    @Test
    internal fun `Should return middle games page for league after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val games = List(8) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games, PageRequest.of(0, 8), 10)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.byLeagueIdAfter(leagueId, 8, afterCursor)
        // then
        assertGamesPage(games, true, true, page)
    }

    @Test
    internal fun `Should return last games page for league after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val games = List(7) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games, PageRequest.of(0, 7), 7)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.byLeagueIdAfter(leagueId, 7, afterCursor)
        // then
        assertGamesPage(games, true, false, page)
    }

    @Test
    internal fun `Should return middle games page for league before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val games = List(9) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampGreaterThanOrderByTimestampAsc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games.reversed(), PageRequest.of(0, 9), 11)
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.byLeagueIdBefore(leagueId, 9, beforeCursor)
        // then
        assertGamesPage(games, true, true, page)
    }

    @Test
    internal fun `Should return first games page for league before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val games = List(4) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampGreaterThanOrderByTimestampAsc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games.reversed(), PageRequest.of(0, 4), 4)
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.byLeagueIdBefore(leagueId, 4, beforeCursor)
        // then
        assertGamesPage(games, false, true, page)
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
        assertGamesPage(games, false, true, page)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return middle completed games page for league after given cursor`() {
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
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games, PageRequest.of(0, 8), 10)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.completedByLeagueIdAfter(leagueId, 8, afterCursor)
        // then
        assertGamesPage(games, true, true, page)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return last completed games page for league after given cursor`() {
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
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games, PageRequest.of(0, 7), 7)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.completedByLeagueIdAfter(leagueId, 7, afterCursor)
        // then
        assertGamesPage(games, true, false, page)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return middle completed games page for league before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val games = List(12) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampGreaterThanAndResultNotNullOrderByTimestampAsc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games.reversed(), PageRequest.of(0, 12), 25)
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.completedByLeagueIdBefore(leagueId, 12, beforeCursor)
        // then
        assertGamesPage(games, true, true, page)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return first completed games page for league before given cursor`() {
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
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampGreaterThanAndResultNotNullOrderByTimestampAsc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games.reversed(), PageRequest.of(0, 8), 8)
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.completedByLeagueIdBefore(leagueId, 8, beforeCursor)
        // then
        assertGamesPage(games, false, true, page)
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
        assertGamesPage(games, false, true, page)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    @Test
    internal fun `Should return middle scheduled games page for league after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val games = List(8) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games, PageRequest.of(0, 8), 10)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.scheduledByLeagueIdAfter(leagueId, 8, afterCursor)
        // then
        assertGamesPage(games, true, true, page)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    @Test
    internal fun `Should return last scheduled games page for league after given cursor`() {
        // given
        val leagueId = randomNanoId()
        val games = List(7) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games, PageRequest.of(0, 7), 7)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.scheduledByLeagueIdAfter(leagueId, 7, afterCursor)
        // then
        assertGamesPage(games, true, false, page)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    @Test
    internal fun `Should return middle scheduled games page for league before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val games = List(16) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampGreaterThanAndResultNullOrderByTimestampAsc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games.reversed(), PageRequest.of(0, 16), 17)
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.scheduledByLeagueIdBefore(leagueId, 16, beforeCursor)
        // then
        assertGamesPage(games, true, true, page)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    @Test
    internal fun `Should return last scheduled games page for league before given cursor`() {
        // given
        val leagueId = randomNanoId()
        val games = List(5) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt()
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByLeagueIdAndTimestampGreaterThanAndResultNullOrderByTimestampAsc(leagueId, timestamp, ofType(Pageable::class))
        } returns PageImpl(games.reversed(), PageRequest.of(0, 5), 5)
        val beforeCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.scheduledByLeagueIdBefore(leagueId, 5, beforeCursor)
        // then
        assertGamesPage(games, false, true, page)
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
        assertGamesPage(games, false, true, page)
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
        val timestamp = currentTimeMillis()
        every {
            accessor.getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games, PageRequest.of(0, 6), 8)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.byPlayerId(playerId, 6, afterCursor)
        // then
        assertGamesPage(games, true, true, page)
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
        val timestamp = currentTimeMillis()
        every {
            accessor.getByPlayerIdAndTimestampLessThanOrderByTimestampDesc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games, PageRequest.of(0, 6), 6)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.byPlayerId(playerId, 6, afterCursor)
        // then
        assertGamesPage(games, true, false, page)
    }

    @Test
    internal fun `Should return empty completed games page for player`() {
        // given
        val playerId = randomNanoId()
        every { accessor.getByPlayerIdAndResultNotNullOrderByTimestampDesc(playerId, ofType(Pageable::class)) } returns
                PageImpl(emptyList(), PageRequest.of(0, 3), 0)
        // when
        val page = repository.completedByPlayerId(playerId, 3)
        // then
        assertTrue(page.items.isEmpty())
        assertFalse(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return first completed games page for player`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val games = List(3) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        }
        every { accessor.getByPlayerIdAndResultNotNullOrderByTimestampDesc(playerId, ofType(Pageable::class)) } returns
                PageImpl(games, PageRequest.of(0, 3), 8)
        // when
        val page = repository.completedByPlayerId(playerId, 3)
        // then
        assertGamesPage(games, false, true, page)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return middle completed games page for player`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val games = List(6) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByPlayerIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games, PageRequest.of(0, 6), 8)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.completedByPlayerId(playerId, 6, afterCursor)
        // then
        assertGamesPage(games, true, true, page)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return last completed games page for player`() {
        // given
        val leagueId = randomNanoId()
        val playerId = randomNanoId()
        val games = List(6) {
            GameEntity(
                randomNanoId(), leagueId, LocalDateTime.now(),
                playerId, "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                randomNanoId(), "Player-${Random.nextInt()}", Random.nextInt(), Random.nextInt(),
                Result(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt())
            )
        }
        val timestamp = currentTimeMillis()
        every {
            accessor.getByPlayerIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games, PageRequest.of(0, 6), 6)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.completedByPlayerId(playerId, 6, afterCursor)
        // then
        assertGamesPage(games, true, false, page)
        page.items.forEach { item -> assertNotNull(item.node.result) }
    }

    @Test
    internal fun `Should return empty scheduled games page for player`() {
        // given
        val playerId = randomNanoId()
        every { accessor.getByPlayerIdAndResultNullOrderByTimestampDesc(playerId, ofType(Pageable::class)) } returns
                PageImpl(emptyList(), PageRequest.of(0, 3), 0)
        // when
        val page = repository.scheduledByPlayerId(playerId, 3)
        // then
        assertTrue(page.items.isEmpty())
        assertFalse(page.hasPreviousPage)
        assertFalse(page.hasNextPage)
    }

    @Test
    internal fun `Should return first scheduled games page for player`() {
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
        every { accessor.getByPlayerIdAndResultNullOrderByTimestampDesc(playerId, ofType(Pageable::class)) } returns
                PageImpl(games, PageRequest.of(0, 3), 8)
        // when
        val page = repository.scheduledByPlayerId(playerId, 3)
        // then
        assertGamesPage(games, false, true, page)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    @Test
    internal fun `Should return middle scheduled games page for player`() {
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
        val timestamp = currentTimeMillis()
        every {
            accessor.getByPlayerIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games, PageRequest.of(0, 6), 8)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.scheduledByPlayerId(playerId, 6, afterCursor)
        // then
        assertGamesPage(games, true, true, page)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    @Test
    internal fun `Should return last scheduled games page for player`() {
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
        val timestamp = currentTimeMillis()
        every {
            accessor.getByPlayerIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(
                playerId, timestamp, ofType(Pageable::class)
            )
        } returns PageImpl(games, PageRequest.of(0, 6), 6)
        val afterCursor = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())
        // when
        val page = repository.scheduledByPlayerId(playerId, 6, afterCursor)
        // then
        assertGamesPage(games, true, false, page)
        page.items.forEach { item -> assertNull(item.node.result) }
    }

    private fun assertGamesPage(
        expectedGames: List<GameEntity>,
        expectedHasPreviousPage: Boolean, expectedHasNextPage: Boolean,
        page: Page<Game>
    ) {
        assertEquals(expectedGames.size, page.items.size)
        assertEquals(expectedGames.first().id, page.items.first().node.id)
        assertEquals(expectedGames.last().id, page.items.last().node.id)
        assertEquals(expectedHasPreviousPage, page.hasPreviousPage)
        assertEquals(expectedHasNextPage, page.hasNextPage)
    }
}