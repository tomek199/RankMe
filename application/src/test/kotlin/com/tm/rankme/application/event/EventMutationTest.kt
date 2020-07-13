package com.tm.rankme.application.event

import com.tm.rankme.application.any
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class EventMutationTest {
    private val competitorRepository = Mockito.mock(CompetitorRepository::class.java)
    private val eventRepository = Mockito.mock(EventRepository::class.java)
    private val mapper = EventMapper()
    private val mutation = EventMutation(eventRepository, competitorRepository, mapper)

    private val leagueId = "league-1"
    private val firstCompetitor = Competitor(leagueId, "comp-1", "Batman", Statistics())
    private val secondCompetitor = Competitor(leagueId, "comp-2", "Superman", Statistics())

    @BeforeEach
    internal fun setUp() {
        given(competitorRepository.findById(firstCompetitor.id!!)).willReturn(firstCompetitor)
        given(competitorRepository.findById(secondCompetitor.id!!)).willReturn(secondCompetitor)
    }

    @Test
    internal fun `Should add new event`() {
        // given
        val memberOne = Member(firstCompetitor.id!!, firstCompetitor.username, 314, 1643)
        val memberTwo = Member(secondCompetitor.id!!, secondCompetitor.username, 156, 2895)
        val expectedEvent = Event("event-1", leagueId, memberOne, memberTwo, LocalDateTime.now())
        given(eventRepository.save(any(Event::class.java))).willReturn(expectedEvent)
        // when
        val event = mutation.addEvent(leagueId, firstCompetitor.id!!, secondCompetitor.id!!, LocalDateTime.now())
        // then
        assertNotNull(event)
        verify(competitorRepository, times(1)).findById(firstCompetitor.id!!)
        verify(competitorRepository, times(1)).findById(secondCompetitor.id!!)
        verify(eventRepository, only()).save(any(Event::class.java))
    }

    @Test
    internal fun `Should throw exception when first competitor does not exist for event`() {
        // given
        val invalidCompetitorId = "comp-3"
        given(competitorRepository.findById("comp-3")).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            mutation.addEvent(leagueId, invalidCompetitorId, secondCompetitor.id!!, LocalDateTime.now())
        }
        // then
        assertEquals("Competitor $invalidCompetitorId is not found", exception.message)
    }

    @Test
    internal fun `Should throw exception when second competitor does not exist for event`() {
        // given
        val invalidCompetitorId = "comp-3"
        given(competitorRepository.findById("comp-3")).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            mutation.addEvent(leagueId, firstCompetitor.id!!, invalidCompetitorId, LocalDateTime.now())
        }
        // then
        assertEquals("Competitor $invalidCompetitorId is not found", exception.message)
    }

    @Test
    internal fun `Should throw exception when first player is not included to league for event`() {
        // given
        val invalidCompetitor = Competitor("league-2", "comp-3", "Joker", Statistics())
        given(competitorRepository.findById(invalidCompetitor.id!!)).willReturn(invalidCompetitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            mutation.addEvent(leagueId, invalidCompetitor.id!!, secondCompetitor.id!!, LocalDateTime.now())
        }
        // then
        assertEquals("Competitor comp-3 is not assigned to league $leagueId", exception.message)
    }

    @Test
    internal fun `Should throw exception when second player is not included to league for event`() {
        // given
        val invalidCompetitor = Competitor("league-2", "comp-3", "Joker", Statistics())
        given(competitorRepository.findById(invalidCompetitor.id!!)).willReturn(invalidCompetitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            mutation.addEvent(leagueId, firstCompetitor.id!!, invalidCompetitor.id!!, LocalDateTime.now())
        }
        // then
        assertEquals("Competitor comp-3 is not assigned to league $leagueId", exception.message)
    }
}