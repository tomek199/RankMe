package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitorId1
import com.tm.rankme.domain.competitorId2
import com.tm.rankme.domain.competitorName1
import com.tm.rankme.domain.competitorName2
import com.tm.rankme.domain.gameId
import com.tm.rankme.domain.leagueId
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class GameTest {
    @Test
    internal fun `Should create game without result`() {
        // given
        val playerOne = Player(competitorId1, competitorName1, 258, 1830)
        val playerTwo = Player(competitorId2, competitorName2, 186, 2194)
        val dateTime = LocalDateTime.now()
        // when
        val game = Game(gameId, playerOne, playerTwo, leagueId, dateTime)
        // then
        assertEquals(leagueId, game.leagueId)
        assertEquals(gameId, game.id)
        assertEquals(dateTime, game.dateTime)
        assertEquals(playerOne, game.playerOne)
        assertEquals(playerTwo, game.playerTwo)
        assertEquals(258, playerOne.deviation)
        assertEquals(1830, playerOne.rating)
        assertNull(playerOne.result)
        assertEquals(186, playerTwo.deviation)
        assertEquals(2194, playerTwo.rating)
        assertNull(playerTwo.result)
    }

    @Test
    internal fun `Should create game with result`() {
        // given
        val playerOne = Player(competitorId1, competitorName1, 180, 1343, Result(2, -12, 48))
        val playerTwo = Player(competitorId2, competitorName2, 230, 1758, Result(1, -8,-48))
        val dateTime = LocalDateTime.now()
        // when
        val game = Game(gameId, playerOne, playerTwo, leagueId, dateTime)
        // then
        assertEquals(leagueId, game.leagueId)
        assertEquals(gameId, game.id)
        assertEquals(dateTime, game.dateTime)
        assertEquals(playerOne, game.playerOne)
        assertEquals(playerTwo, game.playerTwo)
        assertEquals(180 - 12, playerOne.deviation)
        assertEquals(1343 + 48, playerOne.rating)
        assertEquals(2, playerOne.result!!.score)
        assertEquals(230 - 8, playerTwo.deviation)
        assertEquals(1758 - 48, playerTwo.rating)
        assertEquals(1, playerTwo.result!!.score)
    }

    @Test
    internal fun `Should complete game`() {
        // given
        val playerOne = Player(competitorId1, competitorName1, 350, 1500)
        val playerTwo = Player(competitorId2, competitorName2, 350, 1500)
        val dateTime = LocalDateTime.now()
        val game = Game(gameId, playerOne, playerTwo, leagueId, dateTime)
        // when
        game.complete(193, 1736, 1, 147, 2093, 3)
        // then
        assertEquals(leagueId, game.leagueId)
        assertEquals(gameId, game.id)
        assertEquals(dateTime, game.dateTime)
        assertEquals(playerOne, game.playerOne)
        assertEquals(playerTwo, game.playerTwo)
        assertEquals(183, playerOne.deviation)
        assertEquals(1736 - 23, playerOne.rating)
        assertEquals(1, playerOne.result!!.score)
        assertEquals(142, playerTwo.deviation)
        assertEquals(2093 + 15, playerTwo.rating)
        assertEquals(3, playerTwo.result!!.score)
    }
}
