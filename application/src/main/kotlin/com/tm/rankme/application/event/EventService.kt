package com.tm.rankme.application.event

import com.tm.rankme.domain.event.Event
import java.time.LocalDateTime

interface EventService {
    fun get(eventId: String): Event
    fun create(
        leagueId: String,
        firstMemberId: String, secondMemberId: String,
        dateTime: LocalDateTime
    ): EventModel

    fun remove(eventId: String)
}