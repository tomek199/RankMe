package com.tm.rankme.application.event

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.event.Event
import java.time.LocalDateTime

interface EventService {
    fun get(eventId: String): Event
    fun create(
        leagueId: String,
        firstCompetitor: Competitor, secondCompetitor: Competitor,
        dateTime: LocalDateTime
    ): Event

    fun remove(eventId: String)
}