package com.tm.rankme.application.competitor

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import org.springframework.stereotype.Service

@Service
class CompetitorMutation(
        private val repository: CompetitorRepository,
        private val mapper: Mapper<Competitor, CompetitorModel>
) : GraphQLMutationResolver {
    fun addCompetitor(leagueId: String, username: String): CompetitorModel {
        val domain = Competitor(leagueId, username)
        val competitor = repository.save(domain)
        return mapper.toModel(competitor)
    }
}