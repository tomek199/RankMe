package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class GameTest {
    @Test
    internal fun `should throw exception when first competitor id is null`() {
        // given
        val leagueId = "league-111"
        // when
        val competitorOne = Competitor(leagueId, "Darth Vader")
        val competitorTwo = Competitor(leagueId, "competitor-111", "Han Solo", Statistics())
        // then
        assertFailsWith<IllegalStateException> { Game(competitorOne, competitorTwo, LocalDate.now()) }
    }

    @Test
    internal fun `should throw exception when second competitor id is null`() {
        // given
        val leagueId = "league-111"
        // when
        val competitorOne = Competitor(leagueId, "competitor-111" ,"Darth Vader", Statistics())
        val competitorTwo = Competitor(leagueId, "Han Solo")
        // then
        assertFailsWith<IllegalStateException> { Game(competitorOne, competitorTwo, LocalDate.now()) }
    }

    @Test
    internal fun `should create scheduled game without score`() {
        // given
        val leagueId = "league-111"
        val date = LocalDate.now()
        val statisticsOne = Statistics(204, 1344, 49, 38, 8, date)
        val competitorOne = Competitor(leagueId, "c-111" ,"Darth Vader", statisticsOne)
        val statisticsTwo = Statistics(279, 2043, 98, 93, 25, date)
        val competitorTwo = Competitor(leagueId, "c-222" ,"Han Solo", statisticsTwo)
        // when
        val game = Game(competitorOne, competitorTwo, date)
        // then
        assertNull(game.id)

        assertEquals(date, game.date)
        assertEquals(competitorOne.id, game.playerOne.competitorId)
        assertEquals(competitorOne.username, game.playerOne.username)
        assertEquals(competitorOne.statistics.deviation, game.playerOne.deviation)
        assertEquals(competitorOne.statistics.rating, game.playerOne.rating)
        assertNull(game.playerOne.score)
        assertNull(game.playerOne.ratingDelta)

        assertEquals(date, game.date)
        assertEquals(competitorTwo.id, game.playerTwo.competitorId)
        assertEquals(competitorTwo.username, game.playerTwo.username)
        assertEquals(competitorTwo.statistics.deviation, game.playerTwo.deviation)
        assertEquals(competitorTwo.statistics.rating, game.playerTwo.rating)
        assertNull(game.playerTwo.score)
        assertNull(game.playerTwo.ratingDelta)
    }

    @Test
    internal fun `should complete scheduled game`() {
        // given
        val date = LocalDate.now()
        val leagueId = "league-111"
        val oneStats = Statistics(283, 1847, 0, 0, 0, date)
        val competitorTwo = Competitor(leagueId, "c-222" ,"Han Solo", oneStats)
        val twoStats = Statistics(165, 2156, 0, 0, 0, date)
        val competitorOne = Competitor(leagueId, "c-111" ,"Darth Vader", twoStats)
        val game = Game(competitorOne, competitorTwo, date)
        // when
        game.complete(Pair(oneStats, 1), Pair(twoStats, 2), date)
        // then
        assertEquals(date, game.date)

        assertEquals(game.playerOne.deviation, 252)
        assertEquals(game.playerOne.rating, 1792)
        assertEquals(game.playerOne.ratingDelta, -55)

        assertEquals(game.playerTwo.deviation, 165)
        assertEquals(game.playerTwo.rating, 2180)
        assertEquals(game.playerTwo.ratingDelta, 24)
    }
}
