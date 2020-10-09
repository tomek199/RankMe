package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitorId1
import com.tm.rankme.domain.competitorId2
import com.tm.rankme.domain.competitorName1
import com.tm.rankme.domain.competitorName2
import com.tm.rankme.domain.leagueId
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class GameFactoryTest {
    @Test
    internal fun `Should throw exception when first competitor id is null`() {
        // given
        val competitorTwo = Competitor(leagueId, competitorId2, competitorName2)
        val competitorOne = Competitor(leagueId, competitorName1)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            GameFactory.create(competitorOne, 2, competitorTwo, 1, leagueId)
        }
        // then
        assertEquals("Competitor id cannot be null!", exception.message)
    }

    @Test
    internal fun `Should throw exception when second competitor id is null`() {
        // given
        val competitorOne = Competitor(leagueId, competitorId1, competitorName1)
        val competitorTwo = Competitor(leagueId, competitorName2)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            GameFactory.create(competitorOne, 3, competitorTwo, 2, leagueId)
        }
        // then
        assertEquals("Competitor id cannot be null!", exception.message)

    }

    @Test
    internal fun `Should create game`() {
        // given
        val lastGameDate = LocalDate.now()
        val competitorOne = Competitor(leagueId, competitorId1, competitorName1, 245, 1397, lastGameDate)
        val competitorTwo = Competitor(leagueId, competitorId2, competitorName2, 224, 1874, lastGameDate)
        // when
        val game = GameFactory.create(competitorOne, 1, competitorTwo, 0, leagueId)
        // then
        assertNotNull(game.dateTime)
        assertEquals(leagueId, game.leagueId)

        assertEquals(236, game.playerOne.deviation)
        assertEquals(1631, game.playerOne.rating)
        assertEquals(234, game.playerOne.result.ratingDelta)

        assertEquals(218, game.playerTwo.deviation)
        assertEquals(1681, game.playerTwo.rating)
        assertEquals(-193, game.playerTwo.result.ratingDelta)
    }
}