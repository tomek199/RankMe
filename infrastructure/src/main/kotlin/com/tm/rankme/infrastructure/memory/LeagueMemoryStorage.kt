package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository

class LeagueMemoryStorage  : LeagueRepository {
    private val leagues: MutableCollection<League> = mutableListOf()

    override fun save(league: League): League {
        league.id = (leagues.size + 1).toString()
        leagues.add(league)
        return league
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