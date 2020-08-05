package com.tm.rankme.application.event

import com.tm.rankme.domain.event.Event

interface EventService {
    fun get(eventId: String): Event
    fun create(event: Event): Event
    fun remove(eventId: String)
}