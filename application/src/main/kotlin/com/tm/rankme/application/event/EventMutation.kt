package com.tm.rankme.application.event

import com.tm.rankme.application.common.logger
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class EventMutation(private val eventService: EventService) : GraphQLMutationResolver {
    private val log = logger<EventMutation>()

    fun addEvent(input: AddEventInput): EventModel {
        log.info("Add event: $input")
        return eventService.create(input.leagueId, input.memberOneId, input.memberTwoId, input.dateTime)
    }
}
