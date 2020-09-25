package com.tm.rankme.application.match

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.match.Match
import com.tm.rankme.domain.match.Member
import org.springframework.stereotype.Service

@Service
class MatchMapper : Mapper<Match, MatchModel> {
    override fun toModel(domain: Match): MatchModel {
        val id = domain.id ?: throw IllegalStateException("Match id can't be null!")
        val memberOne = mapMember(domain.memberOne)
        val memberTwo = mapMember(domain.memberTwo)
        return MatchModel(id, memberOne, memberTwo, domain.dateTime)
    }

    private fun mapMember(member: Member): MemberModel {
        return MemberModel(member.competitorId, member.username, member.deviation, member.rating)
    }
}