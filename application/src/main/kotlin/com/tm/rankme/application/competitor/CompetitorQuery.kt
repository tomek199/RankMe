package com.tm.rankme.application.competitor

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class CompetitorQuery(
        private val repository: CompetitorRepository,
        @Qualifier("competitorMapper") private val mapper: Mapper<Competitor, CompetitorModel>
) : GraphQLQueryResolver {
    fun competitor(id: String): CompetitorModel? {
        val competitor = repository.findById(id)
        return competitor?.let { mapper.toModel(competitor) }
    }
}
