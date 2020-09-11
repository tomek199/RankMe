package com.tm.rankme.application.event

import com.tm.rankme.application.competitor.CompetitorService
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class EventMutation(
    private val eventService: EventService,
    private val competitorService: CompetitorService
) : GraphQLMutationResolver {

    fun addEvent(input: AddEventInput): EventModel {
        val firstCompetitor = competitorService.getForLeague(input.memberOneId, input.leagueId)
        val secondCompetitor = competitorService.getForLeague(input.memberTwoId, input.leagueId)
        return eventService.create(input.leagueId, firstCompetitor, secondCompetitor, input.dateTime)
    }
}
