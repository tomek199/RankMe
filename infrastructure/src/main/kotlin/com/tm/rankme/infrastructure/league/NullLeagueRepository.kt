package com.tm.rankme.infrastructure.league

import com.tm.rankme.domain.base.AggregateException
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import java.util.*
import org.springframework.stereotype.Repository

@Repository
class NullLeagueRepository : LeagueRepository {
    override fun byId(id: UUID): League {
        throw AggregateException("Not implemented yet")
    }

    override fun store(aggregate: League) {
        println(aggregate)
        // do nothing
    }
}