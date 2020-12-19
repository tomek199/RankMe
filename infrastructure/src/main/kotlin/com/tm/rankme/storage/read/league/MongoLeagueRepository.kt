package com.tm.rankme.storage.read.league

import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MongoLeagueRepository @Autowired constructor(
    private val leagueAccessor: MongoLeagueAccessor
) : LeagueRepository {

    override fun byId(id: UUID): League? = leagueAccessor.findByIdOrNull(id)?.let {
        League(it.id, it.name, it.allowDraws, it.maxScore)
    }
}