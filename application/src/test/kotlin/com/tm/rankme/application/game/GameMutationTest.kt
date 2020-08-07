package com.tm.rankme.application.game

import com.tm.rankme.application.any
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.event.EventService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.Member
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class GameMutationTest {
    private val gameService = mock(GameService::class.java)
    private val eventService = mock(EventService::class.java)
    private val competitorService = mock(CompetitorService::class.java)
    private val mapper = GameMapper()
    private val mutation = GameMutation(gameService, eventService, competitorService, mapper)

    private val leagueId = "league-1"
    private val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", Statistics())
    private val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", Statistics())

    @BeforeEach
    internal fun setUp() {
        given(competitorService.getForLeague(firstCompetitor.id!!, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitor.id!!, leagueId)).willReturn(secondCompetitor)
    }

    @Test
    internal fun `Should add new game`() {
        // given
        val playerOne = Player(firstCompetitor.id!!, firstCompetitor.username, 274, 1546, 1, -79)
        val playerTwo = Player(secondCompetitor.id!!, secondCompetitor.username, 152, 2587, 3, 79)
        val expectedGame = Game("game-1", playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(gameService.create(leagueId, firstCompetitor, 2, secondCompetitor, 1)).willReturn(expectedGame)
        val input = AddGameInput(leagueId, firstCompetitor.id!!, 2, secondCompetitor.id!!, 1)
        // when
        val game = mutation.addGame(input)
        // then
        assertNotNull(game)
        verify(competitorService, times(1)).getForLeague(firstCompetitor.id!!, leagueId)
        verify(competitorService, times(1)).getForLeague(secondCompetitor.id!!, leagueId)
        verify(competitorService, times(1))
            .updateStatistic(any(Competitor::class.java), any(Competitor::class.java), any(Game::class.java))
        verify(gameService, only()).create(leagueId, firstCompetitor, 2, secondCompetitor, 1)
    }

    @Test
    internal fun `Should create game base on existing event`() {
        // given
        val eventId = "event-1"
        val playerOne = Player(firstCompetitor.id!!, firstCompetitor.username, 274, 1546, 1, -79)
        val playerTwo = Player(secondCompetitor.id!!, secondCompetitor.username, 152, 2587, 3, 79)
        val expectedGame = Game("game-1", playerOne, playerTwo, leagueId, LocalDateTime.now())
        given(gameService.create(leagueId, firstCompetitor, 1, secondCompetitor, 3)).willReturn(expectedGame)
        val event = Event(
            eventId, leagueId,
            Member(firstCompetitor.id!!, firstCompetitor.username, 274, 1546),
            Member(secondCompetitor.id!!, secondCompetitor.username, 152, 2587), LocalDateTime.now()
        )
        given(eventService.get(eventId)).willReturn(event)
        val input = CompleteGameInput(eventId, 1, 3)
        // when
        val game = mutation.completeGame(input)
        // then
        assertNotNull(game)
        verify(eventService, times(1)).get(eventId)
        verify(eventService, times(1)).remove(eventId)
        verify(competitorService, times(1)).getForLeague(firstCompetitor.id!!, leagueId)
        verify(competitorService, times(1)).getForLeague(secondCompetitor.id!!, leagueId)
        verify(competitorService, times(1))
            .updateStatistic(any(Competitor::class.java), any(Competitor::class.java), any(Game::class.java))
        verify(gameService, only()).create(leagueId, firstCompetitor, 1, secondCompetitor, 3)
    }

    @Test
    internal fun `Should throw exception when event does not exist when completing game`() {
        // given
        val eventId = "event-1"
        given(eventService.get(eventId)).willThrow(IllegalStateException::class.java)
        val input = CompleteGameInput(eventId, 1, 3)
        // then
        assertFailsWith<IllegalStateException> { mutation.completeGame(input) }
    }
}