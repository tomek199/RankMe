package com.tm.rankme.application.event

import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class EventMutation(private val eventService: EventService) : GraphQLMutationResolver {
    fun addEvent(input: AddEventInput): EventModel {
        return eventService.create(input.leagueId, input.memberOneId, input.memberTwoId, input.dateTime)
    }
}
