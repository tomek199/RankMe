package com.tm.rankme.domain.competitor

import com.tm.rankme.domain.game.Player
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class CompetitorTest {
    private val leagueId = "league-1"
    private val id = "comp-1"
    private val username = "Optimus Prime"

    @Test
    internal fun `Should create competitor with default parameters`() {
        // given
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
    internal fun `Should create competitor with id and league statistics`() {
        // given
        val statistics = Statistics()
        statistics.deviation = 200
        statistics.rating = 2000
        statistics.lastGame = LocalDate.of(2019, 1, 1)
        // when
        val competitor = Competitor(leagueId, id, username, statistics)
        // then
        assertEquals(id, competitor.id)
        assertEquals(username, competitor.username)
        assertEquals(Status.ACTIVE, competitor.status)
        assertEquals(statistics, competitor.statistics)
    }

    @Test
    internal fun `Should throw exception when update statistics without score`() {
        // given
        val competitor = Competitor(leagueId, username, Statistics())
        val player = Player(id, username, 234, 2548)
        // when
        val exception = assertFailsWith<IllegalArgumentException> {
            competitor.updateStatistics(player, 2, LocalDateTime.now())
        }
        // then
        assertEquals("Player does not contain score value!", exception.message)
    }

    @Test
    internal fun `Should update statistics by draw game`() {
        // given
        val gameDateTime = LocalDateTime.now()
        val deviationAfterGame = 326
        val ratingAfterGame = 1547
        val player = Player(id, username, deviationAfterGame, ratingAfterGame)
        player.score = 3
        val competitor = Competitor(leagueId, username, Statistics())
        // when
        competitor.updateStatistics(player, 3, gameDateTime)
        // then
        assertEquals(gameDateTime.toLocalDate(), competitor.statistics.lastGame)
        assertEquals(deviationAfterGame + 4 /* getter recalculation */, competitor.statistics.deviation)
        assertEquals(ratingAfterGame, competitor.statistics.rating)
        assertEquals(1, competitor.statistics.draw)
        assertEquals(0, competitor.statistics.won)
        assertEquals(0, competitor.statistics.lost)
    }

    @Test
    internal fun `Should update statistics by won game`() {
        // given
        val competitor = Competitor(leagueId, username, Statistics())
        val gameDateTime = LocalDateTime.now()
        val deviationAfterGame = 258
        val ratingAfterGame = 1673
        val player = Player(id, username, deviationAfterGame, ratingAfterGame)
        player.score = 2
        // when
        competitor.updateStatistics(player, 0, gameDateTime)
        // then
        assertEquals(gameDateTime.toLocalDate(), competitor.statistics.lastGame)
        assertEquals(deviationAfterGame + 4 /* getter recalculation */, competitor.statistics.deviation)
        assertEquals(ratingAfterGame, competitor.statistics.rating)
        assertEquals(0, competitor.statistics.draw)
        assertEquals(1, competitor.statistics.won)
        assertEquals(0, competitor.statistics.lost)
    }

    @Test
    internal fun `Should update statistics by lost game`() {
        // given
        val competitor = Competitor(leagueId, username, Statistics())
        val gameDateTime = LocalDateTime.now()
        val deviationAfterGame = 173
        val ratingAfterGame = 1438
        val player = Player(id, username, deviationAfterGame, ratingAfterGame)
        player.score = 1
        // when
        competitor.updateStatistics(player, 2, gameDateTime)
        // then
        assertEquals(gameDateTime.toLocalDate(), competitor.statistics.lastGame)
        assertEquals(deviationAfterGame + 7 /* getter recalculation */, competitor.statistics.deviation)
        assertEquals(ratingAfterGame, competitor.statistics.rating)
        assertEquals(0, competitor.statistics.draw)
        assertEquals(0, competitor.statistics.won)
        assertEquals(1, competitor.statistics.lost)
    }
}