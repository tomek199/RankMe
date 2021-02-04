package com.tm.rankme.cqrs.query.league

import com.tm.rankme.cqrs.query.QueryHandler
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import org.springframework.stereotype.Service

@Service
class GetLeagueHandler(
    private val repository: LeagueRepository
) : QueryHandler<GetLeagueQuery, League?> {

    override fun handle(query: GetLeagueQuery): League? = repository.byId(query.id)
}