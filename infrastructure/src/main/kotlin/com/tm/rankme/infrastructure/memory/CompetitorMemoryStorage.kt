package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.competitor.Statistics
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("dev")
class CompetitorMemoryStorage : CompetitorRepository {
    private val competitors: MutableList<Competitor> = mutableListOf()

    override fun save(entity: Competitor): Competitor {
        if (entity.id == null) {
            val id = (competitors.size + 1).toString()
            val competitor = Competitor(entity.leagueId, id, entity.username, Statistics())
            competitors.add(competitor)
            return competitor
        }
        return entity
    }

    override fun findAll(): Collection<Competitor> {
        return competitors
    }

    override fun findById(id: String): Competitor? {
        return competitors.find { competitor -> competitor.id.equals(id) }
    }

    override fun delete(id: String) {
        competitors.removeIf { competitor -> competitor.id.equals(id) }
    }
}