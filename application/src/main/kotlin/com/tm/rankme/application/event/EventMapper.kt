package com.tm.rankme.application.event

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.event.Event
import com.tm.rankme.domain.event.Member
import org.springframework.stereotype.Service

@Service
class EventMapper : Mapper<Event, EventModel> {
    override fun toModel(domain: Event): EventModel {
        val id = domain.id ?: throw IllegalStateException("Event id can't be null!")
        val memberOne = mapMember(domain.memberOne)
        val memberTwo = mapMember(domain.memberTwo)
        return EventModel(id, memberOne, memberTwo, domain.dateTime)
    }

    private fun mapMember(member: Member): MemberModel {
        return MemberModel(member.competitorId, member.username, member.deviation, member.rating)
    }
}