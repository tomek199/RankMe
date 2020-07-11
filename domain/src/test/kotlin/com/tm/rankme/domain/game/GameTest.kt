package com.tm.rankme.domain.game

import com.tm.rankme.domain.competitorId1
import com.tm.rankme.domain.competitorId2
import com.tm.rankme.domain.competitorName1
import com.tm.rankme.domain.competitorName2
import com.tm.rankme.domain.gameId
import com.tm.rankme.domain.leagueId
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

internal class GameTest {
    @Test
    internal fun `Should create game with all properties`() {
        // given
        val playerOne = Player(competitorId1, competitorName1, 180, 1343, 2, 48)
        val playerTwo = Player(competitorId2, competitorName2, 230, 1758, 1, -48)
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
}
