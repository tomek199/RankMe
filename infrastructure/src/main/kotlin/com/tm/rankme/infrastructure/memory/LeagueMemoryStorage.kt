package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository

class LeagueMemoryStorage  : LeagueRepository {
    private val leagues: MutableList<League> = mutableListOf()

    override fun save(entity: League): League {
        val id = (leagues.size + 1).toString()
        leagues.add(League(id, entity.name))
        return entity
    }

    override fun findAll(): Collection<League> {
        return leagues
    }

    override fun findById(id: String): League? {
        return leagues.find { league -> league.id.equals(id) }
    }

    override fun delete(id: String) {
        leagues.removeIf { league: League -> league.id.equals(id) }
    }
}