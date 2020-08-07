package com.tm.rankme.application.event

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.event.Member
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
internal class EventServiceImpl(
    private val repository: EventRepository
) : EventService {

    override fun get(eventId: String): Event {
        val event = repository.findById(eventId)
        return event ?: throw IllegalStateException("Event $eventId is not found")
    }

    override fun create(
        leagueId: String,
        firstCompetitor: Competitor, secondCompetitor: Competitor,
        dateTime: LocalDateTime
    ): Event {
        val firstMember = createMember(firstCompetitor)
        val secondMember = createMember(secondCompetitor)
        val event = Event(leagueId, firstMember, secondMember, dateTime)
        return repository.save(event)
    }

    private fun createMember(competitor: Competitor): Member {
        val id = competitor.id ?: throw IllegalStateException("Competitor ${competitor.username} id is null")
        return Member(id, competitor.username, competitor.statistics.deviation, competitor.statistics.rating)
    }

    override fun remove(eventId: String) {
        repository.delete(eventId)
    }
}