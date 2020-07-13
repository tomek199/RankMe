package com.tm.rankme.application.event

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.event.Member
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EventMutation(
    private val eventRepository: EventRepository,
    private val competitorRepository: CompetitorRepository,
    @Qualifier("eventMapper") private val mapper: Mapper<Event, EventModel>
) : GraphQLMutationResolver {

    fun addEvent(leagueId: String, memberOneId: String, memberTwoId: String, dateTime: LocalDateTime): EventModel {
        val firstCompetitor = getCompetitor(memberOneId, leagueId)
        val secondCompetitor = getCompetitor(memberTwoId, leagueId)
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

    private fun getCompetitor(id: String, leagueId: String): Competitor {
        val competitor = competitorRepository.findById(id) ?: throw IllegalStateException("Competitor $id is not found")
        if (competitor.leagueId != leagueId)
            throw IllegalStateException("Competitor $id is not assigned to league $leagueId")
        return competitor
    }
}