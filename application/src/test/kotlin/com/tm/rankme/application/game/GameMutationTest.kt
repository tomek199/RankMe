package com.tm.rankme.application.game

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import com.tm.rankme.domain.game.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class GameMutationTest {
    private val gameRepository = mock(GameRepository::class.java)
    private val competitorRepository = mock(CompetitorRepository::class.java)
    private val mapper = GameMapper()
    private val mutation = GameMutation(gameRepository, competitorRepository, mapper)

    private val leagueId = "league-1"
    private val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", Statistics())
    private val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", Statistics())

    @BeforeEach
    internal fun setUp() {
        given(competitorRepository.findById(firstCompetitor.id!!)).willReturn(firstCompetitor)
        given(competitorRepository.findById(secondCompetitor.id!!)).willReturn(secondCompetitor)
    }

    @Test
    internal fun `Should add new game`() {
        // given
        val playerOne = Player(firstCompetitor.id!!, firstCompetitor.username, 274, 1546)
        val playerTwo = Player(secondCompetitor.id!!, secondCompetitor.username, 152, 2587)
        val expectedGame = Game("game-1", playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(gameRepository.save(any(Game::class.java))).willReturn(expectedGame)
        // when
        val game = mutation.addCompletedGame(leagueId, firstCompetitor.id!!, 2, secondCompetitor.id!!, 1)
        // then
        assertNotNull(game)
        verify(competitorRepository, times(1)).findById(firstCompetitor.id!!)
        verify(competitorRepository, times(1)).findById(secondCompetitor.id!!)
        verify(competitorRepository, times(1)).save(firstCompetitor)
        verify(competitorRepository, times(1)).save(secondCompetitor)
        verify(gameRepository, only()).save(any(Game::class.java))
    }

    @Test
    internal fun `Should throw exception when first competitor does not exist for completed game`() {
        // given
        val invalidCompetitorId = "comp-3"
        given(competitorRepository.findById("comp-3")).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            mutation.addCompletedGame(leagueId, invalidCompetitorId, 2, secondCompetitor.id!!, 1)
        }
        // then
        assertEquals("Competitor $invalidCompetitorId is not found", exception.message)
    }

    @Test
    internal fun `Should throw exception when second competitor does not exist form completed game`() {
        // given
        val invalidCompetitorId = "comp-3"
        given(competitorRepository.findById("comp-3")).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            mutation.addCompletedGame(leagueId, firstCompetitor.id!!, 2, invalidCompetitorId, 1)
        }
        // then
        assertEquals("Competitor $invalidCompetitorId is not found", exception.message)
    }

    @Test
    internal fun `Should throw exception when first player is not included to league for completed game`() {
        // given
        val invalidCompetitor = Competitor("league-2", "comp-3", "Joker", Statistics())
        given(competitorRepository.findById(invalidCompetitor.id!!)).willReturn(invalidCompetitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            mutation.addCompletedGame(leagueId, invalidCompetitor.id!!, 2, secondCompetitor.id!!, 1)
        }
        // then
        assertEquals("Competitor comp-3 is not assigned to league $leagueId", exception.message)
    }

    @Test
    internal fun `Should throw exception when second player is not included to league for completed game`() {
        // given
        val invalidCompetitor = Competitor("league-2", "comp-3", "Joker", Statistics())
        given(competitorRepository.findById(invalidCompetitor.id!!)).willReturn(invalidCompetitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            mutation.addCompletedGame(leagueId, firstCompetitor.id!!, 2, invalidCompetitor.id!!, 1)
        }
        // then
        assertEquals("Competitor comp-3 is not assigned to league $leagueId", exception.message)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any(type)
}