package com.tm.rankme.application.game

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertNotNull

internal class GameMutationTest {
    private val gameService = mock(GameService::class.java)
    private val mutation = GameMutation(gameService)

    private val leagueId = "league-1"
    private val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", Statistics())
    private val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", Statistics())

    @Test
    internal fun `Should add new game`() {
        // given
        val playerOne = PlayerModel(firstCompetitor.id!!, firstCompetitor.username, 274, 1546)
        val playerTwo = PlayerModel(secondCompetitor.id!!, secondCompetitor.username, 152, 2587)
        val expectedGame = GameModel("game-1", playerOne, playerTwo, LocalDateTime.now())
        given(gameService.create(leagueId, firstCompetitor.id!!, 2, secondCompetitor.id!!, 1)).willReturn(expectedGame)
        val input = AddGameInput(leagueId, firstCompetitor.id!!, 2, secondCompetitor.id!!, 1)
        // when
        val game: GameModel = mutation.addGame(input)
        // then
        assertNotNull(game)
        verify(gameService, only()).create(leagueId, firstCompetitor.id!!, 2, secondCompetitor.id!!, 1)
    }

    @Test
    internal fun `Should create game base on existing match`() {
        // given
        val matchId = "match-1"
        val playerOne = PlayerModel(firstCompetitor.id!!, firstCompetitor.username, 274, 1546)
        val playerTwo = PlayerModel(secondCompetitor.id!!, secondCompetitor.username, 152, 2587)
        val expectedGame = GameModel("game-1", playerOne, playerTwo, LocalDateTime.now())
        given(gameService.complete(matchId, 1, 3)).willReturn(expectedGame)
        val input = CompleteGameInput(matchId, 1, 3)
        // when
        val game: GameModel = mutation.completeGame(input)
        // then
        assertNotNull(game)
        verify(gameService, only()).complete(matchId, 1, 3)
    }
}