package com.tm.rankme.application.game

import com.tm.rankme.domain.Side
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.Player
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

internal class GameServiceTest {
    private val repository: GameRepository = Mockito.mock(GameRepository::class.java)
    private val service: GameService = GameServiceImpl(repository)
    private val gameId = "game-1"
    private val leagueId = "league-1"
    private val playerOne = Player("comp-1", "Batman", 235, 1683, 3, 46)
    private val playerTwo = Player("comp-2", "Superman", 386, 2748, 1, -46)

    @Test
    internal fun `Should return game`() {
        val expectedGame = Game(gameId, playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(repository.findById(gameId)).willReturn(expectedGame)
        // when
        val game = service.get(gameId)
        // then
        assertEquals(expectedGame.leagueId, game.leagueId)
        assertEquals(expectedGame.id, game.id)
        assertEquals(expectedGame.playerOne, game.playerOne)
        assertEquals(expectedGame.playerTwo, game.playerTwo)
        assertEquals(expectedGame.dateTime, game.dateTime)
        verify(repository, only()).findById(gameId)
    }

    @Test
    internal fun `Should throw IllegalStateException when game does not exist`() {
        // given
        given(repository.findById(gameId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> { service.get(gameId) }
        // then
        assertEquals("Game $gameId is not found", exception.message)
    }

    @Test
    internal fun `Should create game`() {
        val expectedGame = Game(gameId, playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(repository.save(expectedGame)).willReturn(expectedGame)
        // when
        val game = service.create(expectedGame)
        // then
        assertEquals(expectedGame.leagueId, game.leagueId)
        assertEquals(expectedGame.id, game.id)
        assertEquals(expectedGame.playerOne, game.playerOne)
        assertEquals(expectedGame.playerTwo, game.playerTwo)
        assertEquals(expectedGame.dateTime, game.dateTime)
        verify(repository, only()).save(expectedGame)
    }

    @Test
    internal fun `Should return games side by league id`() {
        // given
        val game = Game(gameId, playerOne, playerTwo, leagueId, LocalDateTime.now())
        val side = Side(listOf(game), 1, hasPrevious = false, hasNext = false)
        given(repository.findByLeagueId(leagueId, 1)).willReturn(side)
        // when
        val result = service.getSideForLeague(leagueId, 1, null)
        // then
        assertEquals(1, result.total)
        assertFalse(result.hasPrevious)
        assertFalse(result.hasNext)
        assertEquals(1, result.content.size)
        val firstGame = result.content.first()
        assertEquals(game.id, firstGame.id)
        assertEquals(game.dateTime, firstGame.dateTime)
        assertEquals(game.id, firstGame.id)
        assertEquals(game.playerOne.competitorId, firstGame.playerOne.competitorId)
        assertEquals(game.playerTwo.competitorId, firstGame.playerTwo.competitorId)
    }
}