package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.competitorId1
import com.tm.rankme.domain.competitorId2
import com.tm.rankme.domain.competitorName1
import com.tm.rankme.domain.competitorName2
import com.tm.rankme.domain.gameId
import com.tm.rankme.domain.leagueId
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GameTest {
    @Test
    internal fun `Should create game with all properties`() {
        // given
        val playerOne = Player(competitorId1, competitorName1, 180, 1343)
        val playerTwo = Player(competitorId2, competitorName2, 230, 1758)
        val dateTime = LocalDateTime.now()
        // when
        val game = Game(gameId, playerOne, playerTwo, leagueId, dateTime)
        // then
        assertEquals(leagueId, game.leagueId)
        assertEquals(gameId, game.id)
        assertEquals(dateTime, game.dateTime)
        assertEquals(playerOne, game.playerOne)
        assertEquals(playerTwo, game.playerTwo)
    }

    @Test
    internal fun `Should complete scheduled game`() {
        // given
        val lastGameDate = LocalDate.now()
        val oneStats = Statistics(283, 1847, 0, 0, 0, lastGameDate)
        val competitorOne = Competitor(leagueId, competitorId1, competitorName1, oneStats)
        val twoStats = Statistics(165, 2156, 0, 0, 0, lastGameDate)
        val competitorTwo = Competitor(leagueId, competitorId2, competitorName2, twoStats)
        val game = GameFactory.scheduledGame(competitorOne, competitorTwo, leagueId, LocalDateTime.now())
        // when
        game.complete(Pair(competitorOne, 1), Pair(competitorTwo, 2))
        // then
        assertNotNull(game.dateTime)

        assertEquals(252, game.playerOne.deviation)
        assertEquals(1792, game.playerOne.rating)
        assertEquals(-55, game.playerOne.ratingDelta)

        assertEquals(165, game.playerTwo.deviation)
        assertEquals(2180, game.playerTwo.rating)
        assertEquals(24, game.playerTwo.ratingDelta)
    }
}
