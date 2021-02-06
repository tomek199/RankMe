package com.tm.rankme.domain.game

import com.tm.rankme.domain.base.AggregateException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class GameTest {
    @Test
    internal fun `Should create played game`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOneId = UUID.randomUUID()
        val playerTwoId = UUID.randomUUID()
        val playerOneResult = Result(2, -34, 57)
        val playerTwoResult = Result(0, -45, -62)
        // when
        val game = Game.played(leagueId, playerOneId, playerTwoId, playerOneResult, playerTwoResult)
        // then
        assertNotNull(game.id)
        assertEquals(0, game.version)
        assertEquals(1, game.pendingEvents.size)
        assertTrue(game.pendingEvents[0] is GamePlayed)
        assertEquals(0, game.pendingEvents[0].version)
        assertEquals(leagueId, game.leagueId)
        assertNotNull(game.dateTime)
        assertEquals(playerOneId, game.playerIds.first)
        assertEquals(playerTwoId, game.playerIds.second)
        assertEquals(playerOneResult, game.result?.first)
        assertEquals(playerTwoResult, game.result?.second)
    }

    @Test
    internal fun `Should create scheduled game`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOneId = UUID.randomUUID()
        val playerTwoId = UUID.randomUUID()
        val dateTime = LocalDateTime.now()
        // when
        val game = Game.scheduled(leagueId, dateTime, playerOneId, playerTwoId)
        // then
        assertNotNull(game.id)
        assertEquals(0, game.version)
        assertEquals(1, game.pendingEvents.size)
        assertTrue(game.pendingEvents[0] is GameScheduled)
        assertEquals(0, game.pendingEvents[0].version)
        assertEquals(leagueId, game.leagueId)
        assertEquals(dateTime.withNano(0), game.dateTime)
        assertEquals(playerOneId, game.playerIds.first)
        assertEquals(playerTwoId, game.playerIds.second)
        assertNull(game.result)
    }

    @Test
    internal fun `Should complete scheduled game`() {
        // given
        val dateTime = LocalDateTime.now()
        val scheduledEvent = GameScheduled(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            dateTime.toEpochSecond(ZoneOffset.UTC))
        val game = Game.from(listOf(scheduledEvent))
        val firstResult = Result(3, -56, 127)
        val secondResult = Result(1, -37, -108)
        // when
        game.complete(firstResult, secondResult)
        // then
        assertNotNull(game.id)
        assertEquals(1, game.version)
        assertEquals(1, game.pendingEvents.size)
        assertTrue(game.pendingEvents[0] is GamePlayed)
        assertEquals(scheduledEvent.leagueId, game.leagueId)
        assertEquals(dateTime.withNano(0), game.dateTime)
        assertEquals(scheduledEvent.firstId, game.playerIds.first)
        assertEquals(scheduledEvent.secondId, game.playerIds.second)
        assertEquals(Pair(firstResult, secondResult), game.result)
    }

    @Test
    internal fun `Should throw exception when try to complete already played game`() {
        // given
        val firstResult = Result(3, -34, 65)
        val secondResult = Result(0, -43, -52)
        val game = Game.played(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), firstResult, secondResult)
        // when
        val exception = assertFailsWith<AggregateException> { game.complete(firstResult, secondResult) }
        // then
        assertEquals("Game ${game.id} is already played", exception.message)
    }

    @Test
    internal fun `Should init game aggregate from 'game-played' event`() {
        // given
        val dateTime = LocalDateTime.now()
        val event = GamePlayed(UUID.randomUUID(), UUID.randomUUID(), 2, -32, -56,
            UUID.randomUUID(), 5, -42, 98,
            dateTime.toEpochSecond(ZoneOffset.UTC), UUID.randomUUID())
        // when
        val game = Game.from(listOf(event))
        // then
        assertEquals(event.aggregateId, game.id)
        assertEquals(event.version, game.version)
        assertEquals(event.leagueId, game.leagueId)
        assertEquals(dateTime.withNano(0), game.dateTime)
        assertEquals(Pair(event.firstId, event.secondId), game.playerIds)
        assertEquals(event.firstDeviationDelta, game.result!!.first.deviationDelta)
        assertEquals(event.firstRatingDelta, game.result!!.first.ratingDelta)
        assertEquals(event.firstScore, game.result!!.first.score)
        assertEquals(event.secondDeviationDelta, game.result!!.second.deviationDelta)
        assertEquals(event.secondRatingDelta, game.result!!.second.ratingDelta)
        assertEquals(event.secondScore, game.result!!.second.score)
    }

    @Test
    internal fun `Should init game aggregate from 'game-scheduled' event`() {
        // given
        val dateTime = LocalDateTime.now()
        val event = GameScheduled(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            dateTime.toEpochSecond(ZoneOffset.UTC), UUID.randomUUID())
        // when
        val game = Game.from(listOf(event))
        // then
        assertEquals(event.aggregateId, game.id)
        assertEquals(event.version, game.version)
        assertEquals(event.leagueId, game.leagueId)
        assertEquals(dateTime.withNano(0), game.dateTime)
        assertEquals(Pair(event.firstId, event.secondId), game.playerIds)
    }

    @Test
    internal fun `Should init game aggregate from all events`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOneId = UUID.randomUUID()
        val playerTwoId = UUID.randomUUID()
        val dateTime = LocalDateTime.now()
        val scheduledEvent = GameScheduled(leagueId, playerOneId, playerTwoId, dateTime.toEpochSecond(ZoneOffset.UTC))
        val playedEvent = GamePlayed(leagueId,
            playerOneId, 2, -45, 127,
            playerTwoId, 0, -59, -142,
            dateTime.toEpochSecond(ZoneOffset.UTC), scheduledEvent.aggregateId, 1
        )
        // when
        val game = Game.from(listOf(scheduledEvent, playedEvent))
        // then
        assertEquals(scheduledEvent.aggregateId, game.id)
        assertEquals(1, game.version)
        assertEquals(leagueId, game.leagueId)
        assertEquals(dateTime.withNano(0), game.dateTime)
        assertEquals(Pair(playerOneId, playerTwoId), game.playerIds)
        assertEquals(-45, game.result!!.first.deviationDelta)
        assertEquals(127, game.result!!.first.ratingDelta)
        assertEquals(2, game.result!!.first.score)
        assertEquals(-59, game.result!!.second.deviationDelta)
        assertEquals(-142, game.result!!.second.ratingDelta)
        assertEquals(0, game.result!!.second.score)
        assertTrue(game.pendingEvents.isEmpty())
    }
}