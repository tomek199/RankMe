package com.tm.rankme.domain.competitor

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class CompetitorTest {
    private val leagueId = "league-1"
    private val id = "comp-1"
    private val username = "Optimus Prime"

    @Test
    internal fun `Should create competitor with default parameters`() {
        // when
        val competitor = Competitor(leagueId, username)
        // then
        assertEquals(leagueId, competitor.leagueId)
        assertEquals(username, competitor.username)
        assertEquals(350, competitor.deviation)
        assertEquals(1500, competitor.rating)
        assertNull(competitor.lastGame)
    }

    @Test
    internal fun `Should create competitor with custom parameters`() {
        // given
        val deviation = 200
        val rating = 2000
        val lastGame = LocalDate.now()
        // when
        val competitor = Competitor(leagueId, id, username, deviation, rating, lastGame)
        // then
        assertEquals(id, competitor.id)
        assertEquals(username, competitor.username)
        assertEquals(deviation + 6, competitor.deviation) // Deviation will be always higher than initialized value
        assertEquals(rating, competitor.rating)
        assertEquals(lastGame, competitor.lastGame)
    }

    @Test
    internal fun `Should update competitor after game`() {
        // given
        val gameDateTime = LocalDateTime.now()
        val deviationAfterGame = 326
        val ratingAfterGame = 1547
        val competitor = Competitor(leagueId, username)
        // when8
        competitor.updateAfterGame(deviationAfterGame, ratingAfterGame, gameDateTime)
        // then
        assertEquals(gameDateTime.toLocalDate(), competitor.lastGame)
        assertEquals(deviationAfterGame + 4 /* getter recalculation */, competitor.deviation)
        assertEquals(ratingAfterGame, competitor.rating)
    }
}