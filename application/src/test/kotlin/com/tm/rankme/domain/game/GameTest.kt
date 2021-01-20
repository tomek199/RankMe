package com.tm.rankme.domain.game

import java.util.*
import kotlin.test.assertEquals
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
        // when
        val game = Game.scheduled(leagueId, playerOneId, playerTwoId)
        // then
        assertNotNull(game.id)
        assertEquals(0, game.version)
        assertEquals(1, game.pendingEvents.size)
        assertTrue(game.pendingEvents[0] is GameScheduled)
        assertEquals(0, game.pendingEvents[0].version)
        assertEquals(leagueId, game.leagueId)
        assertEquals(playerOneId, game.playerIds.first)
        assertEquals(playerTwoId, game.playerIds.second)
        assertNull(game.result)
    }

    @Test
    internal fun `Should init game aggregate from 'game-played' event`() {
        // given
        val event = GamePlayed(UUID.randomUUID(), UUID.randomUUID(), 2, -32, -56,
            UUID.randomUUID(), 5, -42, 98, UUID.randomUUID())
        // when
        val game = Game.from(listOf(event))
        // then
        assertEquals(event.aggregateId, game.id)
        assertEquals(event.version, game.version)
        assertEquals(event.leagueId, game.leagueId)
        assertEquals(Pair(event.firstId, event.secondId), game.playerIds)
        assertEquals(event.firstDeviationDelta, game.result!!.first.deviationDelta)
        assertEquals(event.firstRatingDelta, game.result!!.first.ratingDelta)
        assertEquals(event.firstScore, game.result!!.first.score)
        assertEquals(event.secondDeviationDelta, game.result!!.second.deviationDelta)
        assertEquals(event.secondRatingDelta, game.result!!.second.ratingDelta)
        assertEquals(event.secondScore, game.result!!.second.score)
    }
}