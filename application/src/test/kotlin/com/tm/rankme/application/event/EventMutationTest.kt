package com.tm.rankme.application.event

import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
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
    private val eventService = Mockito.mock(EventService::class.java)
    private val mutation = EventMutation(eventService, competitorService)

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
        val memberOne = MemberModel(firstCompetitor.id!!, firstCompetitor.username, 314, 1643)
        val memberTwo = MemberModel(secondCompetitor.id!!, secondCompetitor.username, 156, 2895)
        val eventDateTime = LocalDateTime.now()
        val expectedEvent = EventModel("event-1", memberOne, memberTwo, eventDateTime)
        given(eventService.create(leagueId, firstCompetitor, secondCompetitor, eventDateTime)).willReturn(expectedEvent)
        val input = AddEventInput(leagueId, firstCompetitor.id!!, secondCompetitor.id!!, eventDateTime)
        // when
        val event: EventModel = mutation.addEvent(input)
        // then
        assertNotNull(event)
        verify(competitorService, times(1)).getForLeague(firstCompetitor.id!!, leagueId)
        verify(competitorService, times(1)).getForLeague(secondCompetitor.id!!, leagueId)
        verify(eventService, only()).create(leagueId, firstCompetitor, secondCompetitor, eventDateTime)
    }
}