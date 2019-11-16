package com.elorating.domain.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LeagueStatsTest {
    @Test
    internal fun `should create LeagueStats with default parameters`() {
        // given
        val leagueId = "11111"
        // when
        val leagueStats = LeagueStats(leagueId)
        // then
        assertEquals(leagueId, leagueStats.leagueId)
        assertEquals(1000, leagueStats.rating)
        assertEquals(0, leagueStats.won)
        assertEquals(0, leagueStats.lost)
        assertEquals(0, leagueStats.draw)
    }
}