package com.tm.rankme.domain.event

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class EventTest {
    private val leagueId = "league-1"
    private val memberOne = Member("member-1", "Batman", 243, 1494)
    private val memberTwo = Member("member-2", "Superman", 185, 2765)

    @Test
    internal fun `Should create event without id`() {
        // given
        val dateTime = LocalDateTime.now()
        // when
        val event = Event(leagueId, memberOne, memberTwo, dateTime)
        // then
        assertNull(event.id)
        assertEquals(leagueId, event.leagueId)
        assertEquals(memberOne, event.memberOne)
        assertEquals(memberTwo, event.memberTwo)
        assertEquals(dateTime, event.dateTime)
    }

    @Test
    internal fun `Should create event with id`() {
        // given
        val id = "event-1"
        val dateTime = LocalDateTime.now()
        // when
        val event = Event(id, leagueId, memberOne, memberTwo, dateTime)
        // then
        assertEquals(id, event.id)
        assertEquals(leagueId, event.leagueId)
        assertEquals(memberOne, event.memberOne)
        assertEquals(memberTwo, event.memberTwo)
        assertEquals(dateTime, event.dateTime)
    }
}