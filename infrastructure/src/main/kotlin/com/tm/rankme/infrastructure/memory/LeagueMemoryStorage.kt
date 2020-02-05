package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("dev")
class LeagueMemoryStorage : LeagueRepository {
    private val leagues: MutableList<League> = mutableListOf()

    override fun save(entity: League): League {
        if (entity.id == null) {
            val id = (leagues.size + 1).toString()
            val league = League(id, entity.name)
            leagues.add(league)
            return league
        }
        return entity
    }

    override fun findAll(): Collection<League> {
        return leagues
    }

    override fun findById(id: String): League? {
        return leagues.find { league -> league.id.equals(id) }
    }

    override fun delete(id: String) {
        leagues.removeIf { league -> league.id.equals(id) }
    }
}