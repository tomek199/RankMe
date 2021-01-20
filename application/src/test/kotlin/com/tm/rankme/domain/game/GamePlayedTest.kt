package com.tm.rankme.domain.game

import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class GamePlayedTest {
    @Test
    internal fun `Should create event`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOneId = UUID.randomUUID()
        val playerTwoId = UUID.randomUUID()
        // when
        val event = GamePlayed(
            leagueId,
            playerOneId, 2, -23, -76,
            playerTwoId, 4, -38, 89
        )
        // then
        assertEquals("game-played", event.type)
        assertNotNull(event.aggregateId)
        assertEquals(0, event.version)
        assertNotNull(event.timestamp)
        assertEquals(leagueId, event.leagueId)
        assertEquals(playerOneId, event.firstId)
        assertEquals(2, event.firstScore)
        assertEquals(-23, event.firstDeviationDelta)
        assertEquals(-76, event.firstRatingDelta)
        assertEquals(playerTwoId, event.secondId)
        assertEquals(4, event.secondScore)
        assertEquals(-38, event.secondDeviationDelta)
        assertEquals(89, event.secondRatingDelta)
    }
}