package com.tm.rankme.application.game

import com.tm.rankme.application.any
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.event.Member
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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class GameMutationTest {
    private val gameRepository = mock(GameRepository::class.java)
    private val eventRepository = mock(EventRepository::class.java)
    private val competitorService = mock(CompetitorService::class.java)
    private val mapper = GameMapper()
    private val mutation = GameMutation(gameRepository, eventRepository, competitorService, mapper)

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
        val input = AddGameInput(leagueId, firstCompetitor.id!!, 2, secondCompetitor.id!!, 1)
        // when
        val game = mutation.addGame(input)
        // then
        assertNotNull(game)
        verify(competitorService, times(1)).getCompetitor(firstCompetitor.id!!, leagueId)
        verify(competitorService, times(1)).getCompetitor(secondCompetitor.id!!, leagueId)
        verify(competitorService, times(1))
            .updateCompetitorsStatistic(any(Competitor::class.java), any(Competitor::class.java), any(Game::class.java))
        verify(gameRepository, only()).save(any(Game::class.java))
    }

    @Test
    internal fun `Should create game base on existing event`() {
        // given
        val eventId = "event-1"
        val playerOne = Player(firstCompetitor.id!!, firstCompetitor.username, 274, 1546, 1, -79)
        val playerTwo = Player(secondCompetitor.id!!, secondCompetitor.username, 152, 2587, 3, 79)
        val expectedGame = Game("game-1", playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(gameRepository.save(any(Game::class.java))).willReturn(expectedGame)
        val event = Event(
            eventId, leagueId,
            Member(firstCompetitor.id!!, firstCompetitor.username, 274, 1546),
            Member(secondCompetitor.id!!, secondCompetitor.username, 152, 2587), LocalDateTime.now()
        )
        given(eventRepository.findById(eventId)).willReturn(event)
        val input = CompleteGameInput(eventId, 1, 3)
        // when
        val game = mutation.completeGame(input)
        // then
        assertNotNull(game)
        verify(eventRepository, times(1)).findById(eventId)
        verify(eventRepository, times(1)).delete(eventId)
        verify(competitorService, times(1)).getCompetitor(firstCompetitor.id!!, leagueId)
        verify(competitorService, times(1)).getCompetitor(secondCompetitor.id!!, leagueId)
        verify(competitorService, times(1))
            .updateCompetitorsStatistic(any(Competitor::class.java), any(Competitor::class.java), any(Game::class.java))
        verify(gameRepository, only()).save(any(Game::class.java))
    }

    @Test
    internal fun `Should throw exception when event does not exist when completing game`() {
        // given
        val eventId = "event-1"
        given(eventRepository.findById(eventId)).willReturn(null)
        val input = CompleteGameInput(eventId, 1, 3)
        // when
        val exception = assertFailsWith<IllegalStateException> { mutation.completeGame(input) }
        // then
        assertEquals("Event $eventId is not found", exception.message)
    }
}