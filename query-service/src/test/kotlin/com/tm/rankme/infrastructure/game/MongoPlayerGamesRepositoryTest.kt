package com.tm.rankme.infrastructure.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.infrastructure.assertGamesPage
import com.tm.rankme.model.game.PlayerGamesRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.lang.System.currentTimeMillis
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class MongoPlayerGamesRepositoryTest {
    private val accessor: MongoGameAccessor = mockk()
    private val repository: PlayerGamesRepository = MongoPlayerGamesRepository(accessor)

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
}