package com.tm.rankme.domain.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class PlayerTest {
    @Test
    internal fun `should create player with default parameters`() {
        // given
        val username = "Optimus Prime"
        val leagueId = "league-111"
        val statistics = Statistics()
        // when
        val player = Player(leagueId, username)
        // then
        assertEquals(leagueId, player.leagueId)
        assertEquals(username, player.username)
        assertEquals(Status.ACTIVE, player.status)
        assertEquals(statistics.deviation, player.statistics.deviation)
        assertEquals(statistics.rating, player.statistics.rating)
        assertEquals(statistics.lastGame, player.statistics.lastGame)
        assertEquals(statistics.draw, player.statistics.draw)
        assertEquals(statistics.lost, player.statistics.lost)
        assertEquals(statistics.won, player.statistics.won)
    }

    @Test
    internal fun `should create player with id and league statistics`() {
        // given
        val leagueId = "league-111"
        val id = "player-111"
        val username = "Optimus Prime"
        val statistics = Statistics()
        statistics.deviation = 200
        statistics.rating = 2000
        statistics.lastGame = LocalDate.of(2019, 1, 1)
        statistics.draw = 5
        statistics.lost = 3
        statistics.won = 7
        // when
        val player = Player(leagueId, id, username, statistics)
        // then
        assertEquals(id, player.id)
        assertEquals(username, player.username)
        assertEquals(Status.ACTIVE, player.status)
        assertEquals(statistics, player.statistics)
    }
}