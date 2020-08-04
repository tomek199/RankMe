package com.tm.rankme.application.event

import com.tm.rankme.application.any
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.event.Member
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.only
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertNotNull

internal class EventMutationTest {
    private val competitorService = Mockito.mock(CompetitorService::class.java)
    private val eventRepository = Mockito.mock(EventRepository::class.java)
    private val mapper = EventMapper()
    private val mutation = EventMutation(eventRepository, competitorService, mapper)

    private val leagueId = "league-1"
    private val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", Statistics())
    private val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", Statistics())

    @BeforeEach
    internal fun setUp() {
        given(competitorService.getForLeague(firstCompetitor.id!!, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitor.id!!, leagueId)).willReturn(secondCompetitor)
    }

    @Test
    internal fun `Should add new event`() {
        // given
        val memberOne = Member(firstCompetitor.id!!, firstCompetitor.username, 314, 1643)
        val memberTwo = Member(secondCompetitor.id!!, secondCompetitor.username, 156, 2895)
        val expectedEvent = Event("event-1", leagueId, memberOne, memberTwo, LocalDateTime.now())
        given(eventRepository.save(any(Event::class.java))).willReturn(expectedEvent)
        val input = AddEventInput(leagueId, firstCompetitor.id!!, secondCompetitor.id!!, LocalDateTime.now())
        // when
        val event = mutation.addEvent(input)
        // then
        assertNotNull(event)
        verify(competitorService, times(1)).getForLeague(firstCompetitor.id!!, leagueId)
        verify(competitorService, times(1)).getForLeague(secondCompetitor.id!!, leagueId)
        verify(eventRepository, only()).save(any(Event::class.java))
    }
}