package com.tm.rankme.domain.match

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class MatchTest {
    private val leagueId = "league-1"
    private val memberOne = Member("member-1", "Batman", 243, 1494)
    private val memberTwo = Member("member-2", "Superman", 185, 2765)

    @Test
    internal fun `Should create match without id`() {
        // given
        val dateTime = LocalDateTime.now()
        // when
        val match = Match(leagueId, memberOne, memberTwo, dateTime)
        // then
        assertNull(match.id)
        assertEquals(leagueId, match.leagueId)
        assertEquals(memberOne, match.memberOne)
        assertEquals(memberTwo, match.memberTwo)
        assertEquals(dateTime, match.dateTime)
    }

    @Test
    internal fun `Should create match with id`() {
        // given
        val id = "match-1"
        val dateTime = LocalDateTime.now()
        // when
        val match = Match(id, leagueId, memberOne, memberTwo, dateTime)
        // then
        assertEquals(id, match.id)
        assertEquals(leagueId, match.leagueId)
        assertEquals(memberOne, match.memberOne)
        assertEquals(memberTwo, match.memberTwo)
        assertEquals(dateTime, match.dateTime)
    }
}