package com.tm.rankme.domain.game

import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
}