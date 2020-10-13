package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import org.springframework.stereotype.Service

@Service
class CompetitorMapper : Mapper<Competitor, CompetitorModel> {
    override fun toModel(domain: Competitor): CompetitorModel {
        val id = domain.id ?: throw IllegalStateException("Competitor id can't be null!")
        return CompetitorModel(id, domain.username, domain.deviation, domain.rating, domain.lastGame)
    }
}