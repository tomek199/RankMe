package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.league.LeagueRepository
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Service

@Service
class CompetitorMutation(
    private val competitorService: CompetitorService,
    private val leagueRepository: LeagueRepository,
    private val mapper: Mapper<Competitor, CompetitorModel>
) : GraphQLMutationResolver {

    fun addCompetitor(input: AddCompetitorInput): CompetitorModel {
        leagueRepository.findById(input.leagueId) ?: throw IllegalStateException("League does not exist!")
        val domain = Competitor(input.leagueId, input.username)
        val competitor = competitorService.saveCompetitor(domain)
        return mapper.toModel(competitor)
    }
}