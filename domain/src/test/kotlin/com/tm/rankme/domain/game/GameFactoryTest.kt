package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitorId1
import com.tm.rankme.domain.competitorId2
import com.tm.rankme.domain.competitorName1
import com.tm.rankme.domain.competitorName2
import com.tm.rankme.domain.leagueId
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class GameFactoryTest {
    @Test
    internal fun `Should throw exception when first competitor id is null when creating scheduled game`() {
        // given
        val competitorTwo = Competitor(leagueId, competitorId2, competitorName2)
        val competitorOne = Competitor(leagueId, competitorName1)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            GameFactory.scheduled(competitorOne, competitorTwo, leagueId, LocalDateTime.now())
        }
        // then
        assertEquals("Competitor id cannot be null!", exception.message)
    }

    @Test
    internal fun `Should throw exception when second competitor id is null when creating scheduled game`() {
        // given
        val competitorOne = Competitor(leagueId, competitorId1, competitorName1)
        val competitorTwo = Competitor(leagueId, competitorName2)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            GameFactory.scheduled(competitorOne, competitorTwo, leagueId, LocalDateTime.now())
        }
        // then
        assertEquals("Competitor id cannot be null!", exception.message)
    }

    @Test
    internal fun `Should create scheduled game`() {
        // given
        val lastGameDate = LocalDate.now()
        val competitorOne = Competitor(leagueId, competitorId1, competitorName1, 245, 1397, lastGameDate)
        val competitorTwo = Competitor(leagueId, competitorId2, competitorName2, 224, 1874, lastGameDate)
        // when
        val game = GameFactory.scheduled(competitorOne, competitorTwo, leagueId, LocalDateTime.now())
        // then
        assertNotNull(game.dateTime)
        assertEquals(leagueId, game.leagueId)

        assertEquals(competitorOne.id, game.playerOne.competitorId)
        assertEquals(competitorOne.username, game.playerOne.username)
        assertEquals(250, game.playerOne.deviation)
        assertEquals(1397, game.playerOne.rating)
        assertNull(game.playerOne.result)

        assertEquals(competitorTwo.id, game.playerTwo.competitorId)
        assertEquals(competitorTwo.username, game.playerTwo.username)
        assertEquals(229, game.playerTwo.deviation)
        assertEquals(1874, game.playerTwo.rating)
        assertNull(game.playerTwo.result)
    }

    @Test
    internal fun `Should throw exception when first competitor id is null when creating completed game`() {
        // given
        val competitorTwo = Competitor(leagueId, competitorId2, competitorName2)
        val competitorOne = Competitor(leagueId, competitorName1)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            GameFactory.completed(competitorOne, 2, competitorTwo, 1, leagueId)
        }
        // then
        assertEquals("Competitor id cannot be null!", exception.message)
    }

    @Test
    internal fun `Should throw exception when second competitor id is null when creating completed game`() {
        // given
        val competitorOne = Competitor(leagueId, competitorId1, competitorName1)
        val competitorTwo = Competitor(leagueId, competitorName2)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            GameFactory.completed(competitorOne, 3, competitorTwo, 2, leagueId)
        }
        // then
        assertEquals("Competitor id cannot be null!", exception.message)
    }

    @Test
    internal fun `Should create completed game`() {
        // given
        val lastGameDate = LocalDate.now()
        val competitorOne = Competitor(leagueId, competitorId1, competitorName1, 245, 1397, lastGameDate)
        val competitorTwo = Competitor(leagueId, competitorId2, competitorName2, 224, 1874, lastGameDate)
        // when
        val game = GameFactory.completed(competitorOne, 1, competitorTwo, 0, leagueId)
        // then
        assertNotNull(game.dateTime)
        assertEquals(leagueId, game.leagueId)

        assertEquals(competitorOne.id, game.playerOne.competitorId)
        assertEquals(competitorOne.username, game.playerOne.username)
        assertEquals(236, game.playerOne.deviation)
        assertEquals(1631, game.playerOne.rating)
        assertEquals(1, game.playerOne.result!!.score)
        assertEquals(234, game.playerOne.result!!.ratingDelta)

        assertEquals(competitorTwo.id, game.playerTwo.competitorId)
        assertEquals(competitorTwo.username, game.playerTwo.username)
        assertEquals(218, game.playerTwo.deviation)
        assertEquals(1681, game.playerTwo.rating)
        assertEquals(0, game.playerTwo.result!!.score)
        assertEquals(-193, game.playerTwo.result!!.ratingDelta)
    }
}