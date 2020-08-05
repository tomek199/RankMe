package com.tm.rankme.application.event

import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import org.springframework.stereotype.Service

@Service
internal class EventServiceImpl(
    private val repository: EventRepository
) : EventService {

    override fun get(eventId: String): Event {
        val event = repository.findById(eventId)
        return event ?: throw IllegalStateException("Event $eventId is not found")
    }

    override fun create(event: Event): Event {
        return repository.save(event)
    }

    override fun remove(eventId: String) {
        repository.delete(eventId)
    }
}