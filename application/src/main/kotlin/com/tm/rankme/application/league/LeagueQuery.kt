package com.tm.rankme.application.league

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class LeagueQuery(
        private val repository: LeagueRepository,
        @Qualifier("leagueMapper") private val mapper: Mapper<League, LeagueModel>
) : GraphQLQueryResolver {
    fun league(id: String): LeagueModel? {
        val league = repository.findById(id)
        return league?.let { mapper.toModel(it) }
    }
}