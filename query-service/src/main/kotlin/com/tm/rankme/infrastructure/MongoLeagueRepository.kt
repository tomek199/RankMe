package com.tm.rankme.infrastructure

import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import java.util.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MongoLeagueRepository(
    private val accessor: MongoLeagueAccessor
) : LeagueRepository {

    override fun byId(id: UUID): League? = accessor.findByIdOrNull(id)?.let {
        League(it.id, it.name, it.allowDraws, it.maxScore)
    }

    override fun store(league: League) {
        val entity = LeagueEntity(league.id, league.name, league.allowDraws, league.maxScore)
        accessor.save(entity)
    }
}