package com.tm.rankme.application.competitor

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.league.LeagueRepository
import org.springframework.stereotype.Service

@Service
class CompetitorMutation(
    private val competitorRepository: CompetitorRepository,
    private val leagueRepository: LeagueRepository,
    private val mapper: Mapper<Competitor, CompetitorModel>
) : GraphQLMutationResolver {

    fun addCompetitor(leagueId: String, username: String): CompetitorModel {
        leagueRepository.findById(leagueId) ?: throw IllegalStateException("League does not exist!")
        val domain = Competitor(leagueId, username)
        val competitor = competitorRepository.save(domain)
        return mapper.toModel(competitor)
    }
}