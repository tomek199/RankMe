package com.elorating.domain.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PlayerTest {
    @Test
    internal fun `should create player with default parameters`() {
        // given
        val username = "Optimus Prime"
        // when
        val player = Player(username)
        // then
        assertEquals(username, player.username)
        assertEquals(Status.ACTIVE, player.status)
        assertTrue(player.leagues.isEmpty())
    }

    @Test
    internal fun `should create player with id and league statistics`() {
        // given
        val id = "player-111"
        val username = "Optimus Prime"
        val leagues = mutableListOf(LeagueStats("league-stats-111"), LeagueStats("league-stats-222"))
        // when
        val player = Player(id, username, leagues)
        // then
        assertEquals(id, player.id)
        assertEquals(username, player.username)
        assertEquals(Status.ACTIVE, player.status)
        assertEquals(leagues, player.leagues)
    }

    @Test
    internal fun `should add new league statistics`() {
        // given
        val player = Player("Optimus Prime")
        val leagueStats = LeagueStats("league-stats-111")
        // when
        player.addLeague(leagueStats)
        // then
        assertEquals(1, player.leagues.size)
    }
}