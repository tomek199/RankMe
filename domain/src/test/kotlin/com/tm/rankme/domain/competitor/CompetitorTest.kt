package com.tm.rankme.domain.competitor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class CompetitorTest {
    @Test
    internal fun `should create competitor with default parameters`() {
        // given
        val username = "Optimus Prime"
        val leagueId = "league-111"
        val statistics = Statistics()
        // when
        val competitor = Competitor(leagueId, username)
        // then
        assertEquals(leagueId, competitor.leagueId)
        assertEquals(username, competitor.username)
        assertEquals(Status.ACTIVE, competitor.status)
        assertEquals(statistics.deviation, competitor.statistics.deviation)
        assertEquals(statistics.rating, competitor.statistics.rating)
        assertEquals(statistics.lastGame, competitor.statistics.lastGame)
        assertEquals(statistics.draw, competitor.statistics.draw)
        assertEquals(statistics.lost, competitor.statistics.lost)
        assertEquals(statistics.won, competitor.statistics.won)
    }

    @Test
    internal fun `should create competitor with id and league statistics`() {
        // given
        val leagueId = "league-111"
        val id = "competitor-111"
        val username = "Optimus Prime"
        val statistics = Statistics()
        statistics.deviation = 200
        statistics.rating = 2000
        statistics.lastGame = LocalDate.of(2019, 1, 1)
        statistics.draw = 5
        statistics.lost = 3
        statistics.won = 7
        // when
        val competitor = Competitor(leagueId, id, username, statistics)
        // then
        assertEquals(id, competitor.id)
        assertEquals(username, competitor.username)
        assertEquals(Status.ACTIVE, competitor.status)
        assertEquals(statistics, competitor.statistics)
    }
}