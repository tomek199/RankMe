package com.tm.rankme.application.league

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.league.League
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class LeagueQuery(
    private val leagueService: LeagueService,
    @Qualifier("leagueMapper") private val mapper: Mapper<League, LeagueModel>
) : GraphQLQueryResolver {

    fun league(id: String): LeagueModel? {
        val league = leagueService.getLeague(id)
        return mapper.toModel(league)
    }
}