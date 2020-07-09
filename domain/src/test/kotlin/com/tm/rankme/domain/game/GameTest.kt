package com.tm.rankme.domain.game

import com.tm.rankme.domain.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

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
}
