package com.tm.rankme.domain.game

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class GameScheduledTest {
    @Test
    internal fun `Should create event`() {
        // given
        val leagueId = UUID.randomUUID()
        val playerOneId = UUID.randomUUID()
        val playerTwoId = UUID.randomUUID()
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