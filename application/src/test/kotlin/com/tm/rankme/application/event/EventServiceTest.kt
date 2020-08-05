package com.tm.rankme.application.event

import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.event.Member
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class EventServiceTest {
    private val repository: EventRepository = Mockito.mock(EventRepository::class.java)
    private val service: EventService = EventServiceImpl(repository)
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
        val expectedEvent = Event("event-1", "league-1", memberOne, memberTwo, LocalDateTime.now())
        given(repository.save(expectedEvent)).willReturn(expectedEvent)
        // when
        val event = service.create(expectedEvent)
        // then
        assertEquals(expectedEvent.id, event.id)
        assertEquals(expectedEvent.leagueId, event.leagueId)
        assertEquals(expectedEvent.memberOne, event.memberOne)
        assertEquals(expectedEvent.memberTwo, event.memberTwo)
        assertEquals(expectedEvent.dateTime, event.dateTime)
        verify(repository, only()).save(expectedEvent)
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