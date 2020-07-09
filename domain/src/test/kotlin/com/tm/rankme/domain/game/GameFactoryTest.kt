package com.tm.rankme.domain.game

import com.tm.rankme.domain.*
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class GameFactoryTest {
    @Test
    internal fun `Should throw exception when first competitor id is null`() {
        // given
        val competitorTwo = Competitor(leagueId, competitorId2, competitorName2, Statistics())
        val competitorOne = Competitor(leagueId, competitorName1)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            GameFactory.create(Pair(competitorOne, 2), Pair(competitorTwo, 1), leagueId)
        }
        // then
        assertEquals("Competitor ids cannot be null!", exception.message)
    }

    @Test
    internal fun `Should throw exception when second competitor id is null`() {
        // given
        val competitorOne = Competitor(leagueId, competitorId1, competitorName1, Statistics())
        val competitorTwo = Competitor(leagueId, competitorName2)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            GameFactory.create(Pair(competitorOne, 3), Pair(competitorTwo, 2), leagueId)
        }
        // then
        assertEquals("Competitor ids cannot be null!", exception.message)

    }

    @Test
    internal fun `Should create completed game`() {
        // given
        val lastGameDate = LocalDate.now()
        val oneStats = Statistics(245, 1397, 0, 0, 0, lastGameDate)
        val competitorOne = Competitor(leagueId, competitorId1, competitorName1, oneStats)
        val twoStats = Statistics(224, 1874, 0, 0, 0, lastGameDate)
        val competitorTwo = Competitor(leagueId, competitorId2, competitorName2, twoStats)
        // when
        val game = GameFactory.create(Pair(competitorOne, 1), Pair(competitorTwo, 0), leagueId)
        // then
        assertNotNull(game.dateTime)
        assertEquals(leagueId, game.leagueId)

        assertEquals(236, game.playerOne.deviation)
        assertEquals(1631, game.playerOne.rating)
        assertEquals(234, game.playerOne.ratingDelta)

        assertEquals(218, game.playerTwo.deviation)
        assertEquals(1681, game.playerTwo.rating)
        assertEquals(-193, game.playerTwo.ratingDelta)
    }
}