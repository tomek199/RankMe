package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
@Qualifier("competitorMapper")
class CompetitorMapper : Mapper<Competitor, CompetitorModel> {
    override fun toModel(domain: Competitor): CompetitorModel {
        val id = domain.id ?: throw IllegalStateException("Competitor id can't be null!")
        val domainStats = domain.statistics
        val statistics = CompetitorStatisticsModel(
            domainStats.deviation, domainStats.rating,
            domainStats.won, domainStats.lost, domainStats.draw, domainStats.lastGame
        )
        return CompetitorModel(id, domain.username, statistics)
    }
}