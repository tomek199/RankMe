package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("dev")
class EventMemoryStorage : EventRepository {
    private val events: MutableList<Event> = mutableListOf()

    override fun findByLeagueId(leagueId: String): List<Event> {
        return events.filter { event -> event.leagueId == leagueId }
    }

    override fun save(entity: Event): Event {
        if (entity.id == null) {
            val id = (events.size + 1).toString()
            val event = Event(id, entity.leagueId, entity.memberOne, entity.memberTwo, entity.dateTime)
            events.add(event)
            return event
        }
        return entity
    }

    override fun findById(id: String): Event? {
        return events.find { event -> event.id.equals(id) }
    }

    override fun delete(id: String) {
        events.removeIf { event -> event.id.equals(id) }
    }
}