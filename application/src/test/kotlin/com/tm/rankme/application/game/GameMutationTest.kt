package com.tm.rankme.application.game

import com.tm.rankme.application.any
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertNotNull

internal class GameMutationTest {
    private val gameRepository = mock(GameRepository::class.java)
    private val competitorService = mock(CompetitorService::class.java)
    private val mapper = GameMapper()
    private val mutation = GameMutation(gameRepository, competitorService, mapper)

    private val leagueId = "league-1"
    private val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", Statistics())
    private val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", Statistics())

    @BeforeEach
    internal fun setUp() {
        given(competitorService.getCompetitor(firstCompetitor.id!!, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getCompetitor(secondCompetitor.id!!, leagueId)).willReturn(secondCompetitor)
    }

    @Test
    internal fun `Should add new game`() {
        // given
        val playerOne = Player(firstCompetitor.id!!, firstCompetitor.username, 274, 1546, 1, -79)
        val playerTwo = Player(secondCompetitor.id!!, secondCompetitor.username, 152, 2587, 3, 79)
        val expectedGame = Game("game-1", playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(gameRepository.save(any(Game::class.java))).willReturn(expectedGame)
        // when
        val game = mutation.addGame(leagueId, firstCompetitor.id!!, 2, secondCompetitor.id!!, 1)
        // then
        assertNotNull(game)
        verify(competitorService, times(1)).getCompetitor(firstCompetitor.id!!, leagueId)
        verify(competitorService, times(1)).getCompetitor(secondCompetitor.id!!, leagueId)
        verify(competitorService, times(1))
            .updateCompetitorsStatistic(any(Competitor::class.java), any(Competitor::class.java), any(Game::class.java))
        verify(gameRepository, only()).save(any(Game::class.java))
    }
}