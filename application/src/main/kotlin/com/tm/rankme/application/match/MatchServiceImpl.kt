package com.tm.rankme.application.match

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.match.Match
import com.tm.rankme.domain.match.MatchRepository
import com.tm.rankme.domain.match.Member
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
internal class MatchServiceImpl(
    private val repository: MatchRepository,
    private val competitorService: CompetitorService,
    private val mapper: Mapper<Match, MatchModel>
) : MatchService {

    override fun get(matchId: String): Match {
        val match = repository.findById(matchId)
        return match ?: throw IllegalStateException("Match $matchId is not found")
    }

    override fun create(
        leagueId: String,
        firstMemberId: String, secondMemberId: String,
        dateTime: LocalDateTime
    ): MatchModel {
        val firstMember = createMember(firstMemberId, leagueId)
        val secondMember = createMember(secondMemberId, leagueId)
        val match = repository.save(Match(leagueId, firstMember, secondMember, dateTime))
        return mapper.toModel(match)
    }

    private fun createMember(competitorId: String, leagueId: String): Member {
        val competitor = competitorService.getForLeague(competitorId, leagueId)
        val id = competitor.id ?: throw IllegalStateException("Competitor ${competitor.username} id is null")
        return Member(id, competitor.username, competitor.statistics.deviation, competitor.statistics.rating)
    }

    override fun remove(matchId: String) {
        repository.delete(matchId)
    }
}