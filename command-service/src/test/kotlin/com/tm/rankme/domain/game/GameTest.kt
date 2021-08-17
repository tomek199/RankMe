package com.tm.rankme.domain.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.tm.rankme.domain.base.AggregateException
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.*

internal class GameTest {
    @Test
    internal fun `Should create played game`() {
        // given
        val leagueId = randomNanoId()
        val playerOneId = randomNanoId()
        val playerTwoId = randomNanoId()
        val playerOneResult = Result(2, 187, -34, 2294, 57)
        val playerTwoResult = Result(0, 234, -45, 1491, -62)
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
        val leagueId = randomNanoId()
        val playerOneId = randomNanoId()
        val playerTwoId = randomNanoId()
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
        val scheduledEvent = GameScheduled(randomNanoId(), randomNanoId(), randomNanoId(),
            dateTime.toEpochSecond(ZoneOffset.UTC))
        val game = Game.from(listOf(scheduledEvent))
        val firstResult = Result(3, 204, -56, 1843, 127)
        val secondResult = Result(1, 279, -37, 1204, -108)
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
        val firstResult = Result(3, 301, -34, 1569, 65)
        val secondResult = Result(0, 204, -43, 1231, -52)
        val game = Game.played(randomNanoId(), randomNanoId(), randomNanoId(), firstResult, secondResult)
        // when
        val exception = assertFailsWith<AggregateException> { game.complete(firstResult, secondResult) }
        // then
        assertEquals("Game ${game.id} is already played", exception.message)
    }

    @Test
    internal fun `Should init game aggregate from 'game-played' event`() {
        // given
        val dateTime = LocalDateTime.now()
        val event = GamePlayed(randomNanoId(), randomNanoId(), 2, 187,-32, 1854,-56,
            randomNanoId(), 5, 249, -42, 1567, 98,
            dateTime.toEpochSecond(ZoneOffset.UTC), randomNanoId())
        // when
        val game = Game.from(listOf(event))
        // then
        assertEquals(event.aggregateId, game.id)
        assertEquals(event.version, game.version)
        assertEquals(event.leagueId, game.leagueId)
        assertEquals(dateTime.withNano(0), game.dateTime)
        assertEquals(Pair(event.firstId, event.secondId), game.playerIds)
        assertEquals(event.firstDeviation, game.result!!.first.deviation)
        assertEquals(event.firstDeviationDelta, game.result!!.first.deviationDelta)
        assertEquals(event.firstRating, game.result!!.first.rating)
        assertEquals(event.firstRatingDelta, game.result!!.first.ratingDelta)
        assertEquals(event.firstScore, game.result!!.first.score)
        assertEquals(event.secondDeviation, game.result!!.second.deviation)
        assertEquals(event.secondDeviationDelta, game.result!!.second.deviationDelta)
        assertEquals(event.secondRating, game.result!!.second.rating)
        assertEquals(event.secondRatingDelta, game.result!!.second.ratingDelta)
        assertEquals(event.secondScore, game.result!!.second.score)
    }

    @Test
    internal fun `Should init game aggregate from 'game-scheduled' event`() {
        // given
        val dateTime = LocalDateTime.now()
        val event = GameScheduled(randomNanoId(), randomNanoId(), randomNanoId(),
            dateTime.toEpochSecond(ZoneOffset.UTC), randomNanoId())
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
        val leagueId = randomNanoId()
        val playerOneId = randomNanoId()
        val playerTwoId = randomNanoId()
        val dateTime = LocalDateTime.now()
        val scheduledEvent = GameScheduled(leagueId, playerOneId, playerTwoId, dateTime.toEpochSecond(ZoneOffset.UTC))
        val playedEvent = GamePlayed(leagueId,
            playerOneId, 2, 212, -45, 1304, 127,
            playerTwoId, 0, 284, -59, 2195, -142,
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
        assertEquals(212, game.result!!.first.deviation)
        assertEquals(-45, game.result!!.first.deviationDelta)
        assertEquals(1304, game.result!!.first.rating)
        assertEquals(127, game.result!!.first.ratingDelta)
        assertEquals(2, game.result!!.first.score)
        assertEquals(284, game.result!!.second.deviation)
        assertEquals(-59, game.result!!.second.deviationDelta)
        assertEquals(2195, game.result!!.second.rating)
        assertEquals(-142, game.result!!.second.ratingDelta)
        assertEquals(0, game.result!!.second.score)
        assertTrue(game.pendingEvents.isEmpty())
    }
}