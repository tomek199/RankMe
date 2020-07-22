package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.Player
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class GameQueryTest {
    private val repository: GameRepository = mock(GameRepository::class.java)
    private val mapper: Mapper<Game, GameModel> = GameMapper()
    private val query: GameQuery = GameQuery(repository, mapper)

    @Test
    internal fun `Should return game by id`() {
        // given
        val playerOne = Player("comp-1", "Batman", 235, 1683, 3, 46)
        val playerTwo = Player("comp-2", "Superman", 386, 2748, 1, -46)
        val gameId = "game-1"
        val expectedGame = Game(gameId, playerOne, playerTwo, "league-1", LocalDateTime.now())
        given(repository.findById(gameId)).willReturn(expectedGame)
        // when
        val game = query.game(gameId)
        // then
        verify(repository, Mockito.only()).findById(gameId)
        assertNotNull(game)
    }

    @Test
    internal fun `Should return null when game is not found`() {
        // given
        val gameId = "game-1"
        given(repository.findById(gameId)).willReturn(null)
        // when
        val game = query.game(gameId)
        // then
        assertNull(game)
    }
}