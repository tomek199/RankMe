package com.tm.rankme.domain.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GamePlayedTest {
    @Test
    internal fun `Should create event`() {
        // given
        val leagueId = randomNanoId()
        val playerOneId = randomNanoId()
        val playerTwoId = randomNanoId()
        // when
        val event = GamePlayed(
            leagueId,
            playerOneId, 2, 294, -23, 1485, -76,
            playerTwoId, 4, 168,  -38, 2173, 89
        )
        // then
        assertEquals("game-played", event.type)
        assertNotNull(event.aggregateId)
        assertEquals(0, event.version)
        assertNotNull(event.timestamp)
        assertEquals(leagueId, event.leagueId)
        assertEquals(playerOneId, event.firstId)
        assertEquals(2, event.firstScore)
        assertEquals(294, event.firstDeviation)
        assertEquals(-23, event.firstDeviationDelta)
        assertEquals(1485, event.firstRating)
        assertEquals(-76, event.firstRatingDelta)
        assertEquals(playerTwoId, event.secondId)
        assertEquals(4, event.secondScore)
        assertEquals(168, event.secondDeviation)
        assertEquals(-38, event.secondDeviationDelta)
        assertEquals(2173, event.secondRating)
        assertEquals(89, event.secondRatingDelta)
        assertNotNull(event.dateTime)
    }
}