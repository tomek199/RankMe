package com.tm.rankme.application.event

import com.tm.rankme.application.any
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.Statistics
import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.event.Member
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class EventServiceTest {
    private val repository: EventRepository = mock(EventRepository::class.java)
    private val competitorService: CompetitorService = mock(CompetitorService::class.java)
    private val mapper: Mapper<Event, EventModel> = EventMapper()
    private val service: EventService = EventServiceImpl(repository, competitorService, mapper)

    private val eventId = "event-1"
    private val leagueId = "league-1"
    private val memberOne = Member("comp-1", "Batman", 234, 2435)
    private val memberTwo = Member("comp-2", "Superman", 315, 1295)

    @Test
    internal fun `Should get event`() {
        // given
        val expectedEvent = Event(eventId, leagueId, memberOne, memberTwo, LocalDateTime.now())
        given(repository.findById(eventId)).willReturn(expectedEvent)
        // when
        val event = service.get(eventId)
        // then
        assertEquals(expectedEvent.id, event.id)
        assertEquals(expectedEvent.leagueId, event.leagueId)
        assertEquals(expectedEvent.memberOne, event.memberOne)
        assertEquals(expectedEvent.memberTwo, event.memberTwo)
        assertEquals(expectedEvent.dateTime, event.dateTime)
        verify(repository, only()).findById(eventId)
    }

    @Test
    internal fun `Should throw IllegalStateException when event does not exist`() {
        // given
        given(repository.findById(eventId)).willReturn(null)
        // when
        val exception = assertFailsWith<IllegalStateException> { service.get(eventId) }
        // then
        assertEquals("Event $eventId is not found", exception.message)
    }

    @Test
    internal fun `Should create event`() {
        // given
        val firstCompetitorStats = Statistics(243, 2945, 3, 5, 9, LocalDate.now())
        val firstCompetitorId = "comp-1"
        val firstCompetitor = Competitor(leagueId, firstCompetitorId, "Batman", firstCompetitorStats)
        val secondCompetitorStats = Statistics(195, 2877, 8, 7, 2, LocalDate.now())
        val secondCompetitorId = "comp-2"
        val secondCompetitor = Competitor(leagueId, secondCompetitorId, "Superman", secondCompetitorStats)
        val eventDateTime = LocalDateTime.now()
        val expectedEvent = Event(
            eventId, leagueId,
            Member(
                firstCompetitorId, firstCompetitor.username,
                firstCompetitor.statistics.deviation, firstCompetitor.statistics.rating),
            Member(
                secondCompetitorId, secondCompetitor.username,
                secondCompetitor.statistics.deviation, secondCompetitor.statistics.rating),
            eventDateTime
        )
        given(competitorService.getForLeague(firstCompetitorId, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitorId, leagueId)).willReturn(secondCompetitor)
        given(repository.save(any(Event::class.java))).willReturn(expectedEvent)
        // when
        val event: EventModel = service.create(leagueId, firstCompetitorId, secondCompetitorId, eventDateTime)
        // then
        verify(repository, only()).save(any(Event::class.java))
        assertEquals(eventDateTime, event.dateTime)
        assertEquals(firstCompetitor.id, event.memberOne.competitorId)
        assertEquals(firstCompetitor.username, event.memberOne.username)
        assertEquals(firstCompetitor.statistics.deviation, event.memberOne.deviation)
        assertEquals(firstCompetitor.statistics.rating, event.memberOne.rating)
        assertEquals(secondCompetitor.id, event.memberTwo.competitorId)
        assertEquals(secondCompetitor.username, event.memberTwo.username)
        assertEquals(secondCompetitor.statistics.deviation, event.memberTwo.deviation)
        assertEquals(secondCompetitor.statistics.rating, event.memberTwo.rating)
    }

    @Test
    internal fun `Should throw IllegalStateException when first competitor does not have id`() {
        // given
        val firstCompetitorId = "comp-1"
        val firstCompetitor = Competitor(leagueId, "Batman", Statistics())
        val secondCompetitorId = "comp-2"
        val secondCompetitor = Competitor(leagueId, secondCompetitorId, "Superman", Statistics())
        given(competitorService.getForLeague(firstCompetitorId, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitorId, leagueId)).willReturn(secondCompetitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            service.create(leagueId, firstCompetitorId, secondCompetitorId, LocalDateTime.now())
        }
        assertEquals("Competitor ${firstCompetitor.username} id is null", exception.message)
    }

    @Test
    internal fun `Should throw IllegalStateException when second competitor does not have id`() {
        // given
        val firstCompetitorId = "comp-1"
        val firstCompetitor = Competitor(leagueId, firstCompetitorId, "Batman", Statistics())
        val secondCompetitorId = "comp-2"
        val secondCompetitor = Competitor(leagueId, "Superman", Statistics())
        given(competitorService.getForLeague(firstCompetitorId, leagueId)).willReturn(firstCompetitor)
        given(competitorService.getForLeague(secondCompetitorId, leagueId)).willReturn(secondCompetitor)
        // when
        val exception = assertFailsWith<IllegalStateException> {
            service.create(leagueId, firstCompetitorId, secondCompetitorId, LocalDateTime.now())
        }
        assertEquals("Competitor ${secondCompetitor.username} id is null", exception.message)
    }

    @Test
    internal fun `Should remove event`() {
        // given
        val eventId = "event-1"
        // when
        service.remove(eventId)
        // then
        verify(repository, only()).delete(eventId)
    }
}