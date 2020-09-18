package com.tm.rankme.application.event

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertNotNull

internal class EventMutationTest {
    private val eventService = Mockito.mock(EventService::class.java)
    private val mutation = EventMutation(eventService)
    private val leagueId = "league-1"

    @Test
    internal fun `Should add new event`() {
        // given
        val memberOneId = "comp-1"
        val memberOne = MemberModel(memberOneId, "Batman", 314, 1643)
        val memberTwoId = "comp-2"
        val memberTwo = MemberModel(memberTwoId, "Superman", 156, 2895)
        val eventDateTime = LocalDateTime.now()
        val expectedEvent = EventModel("event-1", memberOne, memberTwo, eventDateTime)
        given(eventService.create(leagueId, memberOneId, memberTwoId, eventDateTime)).willReturn(expectedEvent)
        val input = AddEventInput(leagueId, memberOneId, memberTwoId, eventDateTime)
        // when
        val event: EventModel = mutation.addEvent(input)
        // then
        assertNotNull(event)
        verify(eventService, only()).create(leagueId, memberOneId, memberTwoId, eventDateTime)
    }
}