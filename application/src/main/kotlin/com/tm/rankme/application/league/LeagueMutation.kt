package com.tm.rankme.application.league

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.league.League
import com.tm.rankme.domain.league.LeagueRepository
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class LeagueMutation(
    private val repository: LeagueRepository,
    @Qualifier("leagueMapper") private val mapper: Mapper<League, LeagueModel>
) : GraphQLMutationResolver {

    fun addLeague(input: AddLeagueInput): LeagueModel {
        val domain = League(input.name)
        val league = repository.save(domain)
        return mapper.toModel(league)
    }
}