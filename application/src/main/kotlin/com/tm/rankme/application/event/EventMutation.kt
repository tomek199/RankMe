package com.tm.rankme.application.event

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.event.Member
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EventMutation(
    private val eventRepository: EventRepository,
    private val competitorService: CompetitorService,
    @Qualifier("eventMapper") private val mapper: Mapper<Event, EventModel>
) : GraphQLMutationResolver {

    fun addEvent(leagueId: String, memberOneId: String, memberTwoId: String, dateTime: LocalDateTime): EventModel {
        val firstCompetitor = competitorService.getCompetitor(memberOneId, leagueId)
        val secondCompetitor = competitorService.getCompetitor(memberTwoId, leagueId)
        val memberOne = Member(
            memberOneId, firstCompetitor.username,
            firstCompetitor.statistics.deviation, firstCompetitor.statistics.rating
        )
        val memberTwo = Member(
            memberTwoId, secondCompetitor.username,
            secondCompetitor.statistics.deviation, secondCompetitor.statistics.rating
        )
        val event = Event(leagueId, memberOne, memberTwo, LocalDateTime.now())
        return mapper.toModel(eventRepository.save(event))
    }
}
