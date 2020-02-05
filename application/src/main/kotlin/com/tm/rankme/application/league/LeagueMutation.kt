package com.tm.rankme.application.league

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.tm.rankme.application.Mapper
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class LeagueMutation(
        private val repository: LeagueRepository,
        @Qualifier("leagueMapper") private val mapper: Mapper<League, LeagueModel>
) : GraphQLMutationResolver {
    fun addLeague(name: String): LeagueModel {
        val domain = League(name)
        val league = repository.save(domain)
        return mapper.toModel(league)
    }
}