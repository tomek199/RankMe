package com.tm.rankme.application.event

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.event.Event
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class EventMutation(
    private val eventService: EventService,
    private val competitorService: CompetitorService,
    @Qualifier("eventMapper") private val mapper: Mapper<Event, EventModel>
) : GraphQLMutationResolver {

    fun addEvent(input: AddEventInput): EventModel {
        val firstCompetitor = competitorService.getForLeague(input.memberOneId, input.leagueId)
        val secondCompetitor = competitorService.getForLeague(input.memberTwoId, input.leagueId)
        val event = eventService.create(input.leagueId, firstCompetitor, secondCompetitor, input.dateTime)
        return mapper.toModel(event)
    }
}
