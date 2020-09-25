package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.match.Match
import com.tm.rankme.domain.match.MatchRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("dev")
class MatchMemoryStorage : MatchRepository {
    private val matches: MutableList<Match> = mutableListOf()

    override fun findByLeagueId(leagueId: String): List<Match> {
        return matches.filter { match -> match.leagueId == leagueId }
    }

    override fun save(entity: Match): Match {
        if (entity.id == null) {
            val id = (matches.size + 1).toString()
            val match = Match(id, entity.leagueId, entity.memberOne, entity.memberTwo, entity.dateTime)
            matches.add(match)
            return match
        }
        return entity
    }

    override fun findById(id: String): Match? {
        return matches.find { match -> match.id.equals(id) }
    }

    override fun delete(id: String) {
        matches.removeIf { match -> match.id.equals(id) }
    }
}