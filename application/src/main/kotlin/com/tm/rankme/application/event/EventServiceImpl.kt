package com.tm.rankme.application.event

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.event.Member
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
internal class EventServiceImpl(
    private val repository: EventRepository,
    private val competitorService: CompetitorService,
    private val mapper: Mapper<Event, EventModel>
) : EventService {

    override fun get(eventId: String): Event {
        val event = repository.findById(eventId)
        return event ?: throw IllegalStateException("Event $eventId is not found")
    }

    override fun create(
        leagueId: String,
        firstMemberId: String, secondMemberId: String,
        dateTime: LocalDateTime
    ): EventModel {
        val firstMember = createMember(firstMemberId, leagueId)
        val secondMember = createMember(secondMemberId, leagueId)
        val event = repository.save(Event(leagueId, firstMember, secondMember, dateTime))
        return mapper.toModel(event)
    }

    private fun createMember(competitorId: String, leagueId: String): Member {
        val competitor = competitorService.getForLeague(competitorId, leagueId)
        val id = competitor.id ?: throw IllegalStateException("Competitor ${competitor.username} id is null")
        return Member(id, competitor.username, competitor.statistics.deviation, competitor.statistics.rating)
    }

    override fun remove(eventId: String) {
        repository.delete(eventId)
    }
}