package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.Player
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class GameQueryTest {
    private val gameService: GameService = mock(GameService::class.java)
    private val mapper: Mapper<Game, GameModel> = GameMapper()
    private val query: GameQuery = GameQuery(gameService, mapper)

    @Test
    internal fun `Should return game by id`() {
        // given
        val playerOne = Player("comp-1", "Batman", 235, 1683, 3, 46)
        val playerTwo = Player("comp-2", "Superman", 386, 2748, 1, -46)
        val gameId = "game-1"
        val expectedGame = Game(gameId, playerOne, playerTwo, "league-1", LocalDateTime.now())
        given(gameService.get(gameId)).willReturn(expectedGame)
        // when
        val game = query.game(gameId)
        // then
        verify(gameService, only()).get(gameId)
        assertNotNull(game)
    }

    @Test
    internal fun `Should throw IllegalStateException when game is not found`() {
        // given
        val gameId = "game-1"
        given(gameService.get(gameId)).willThrow(IllegalStateException::class.java)
        // then
        assertFailsWith<IllegalStateException> { query.game(gameId) }
    }
}