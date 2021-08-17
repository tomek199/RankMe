package com.tm.rankme.domain.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GameScheduledTest {
    @Test
    internal fun `Should create event`() {
        // given
        val leagueId = randomNanoId()
        val playerOneId = randomNanoId()
        val playerTwoId = randomNanoId()
        val dateTime = LocalDateTime.now()
        // when
        val event = GameScheduled(leagueId, playerOneId, playerTwoId, dateTime.toEpochSecond(ZoneOffset.UTC))
        // then
        assertEquals("game-scheduled", event.type)
        assertNotNull(event.aggregateId)
        assertEquals(0, event.version)
        assertNotNull(event.timestamp)
        assertEquals(leagueId, event.leagueId)
        assertEquals(playerOneId, event.firstId)
        assertEquals(playerTwoId, event.secondId)
        assertEquals(dateTime.toEpochSecond(ZoneOffset.UTC), event.dateTime)
    }
}