package com.tm.rankme.domain.event

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class EventTest {
    private val leagueId = "league-1"
    private val playerOne = Player("player-1", "Batman", 243, 1494)
    private val playerTwo = Player("player-2", "Superman", 185, 2765)

    @Test
    internal fun `Should create event without id`() {
        // given
        val dateTime = LocalDateTime.now()
        // when
        val event = Event(leagueId, playerOne, playerTwo, dateTime)
        // then
        assertNull(event.id)
        assertEquals(leagueId, event.leagueId)
        assertEquals(playerOne, event.playerOne)
        assertEquals(playerTwo, event.playerTwo)
        assertEquals(dateTime, event.dateTime)
    }

    @Test
    internal fun `Should create event with id`() {
        // given
        val id = "event-1"
        val dateTime = LocalDateTime.now()
        // when
        val event = Event(id, leagueId, playerOne, playerTwo, dateTime)
        // then
        assertEquals(id, event.id)
        assertEquals(leagueId, event.leagueId)
        assertEquals(playerOne, event.playerOne)
        assertEquals(playerTwo, event.playerTwo)
        assertEquals(dateTime, event.dateTime)
    }
}