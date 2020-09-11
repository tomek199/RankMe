package com.tm.rankme.application.competitor

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.game.Game
import org.springframework.stereotype.Service

@Service
internal class CompetitorServiceImpl(
    private val repository: CompetitorRepository,
    private val mapper: Mapper<Competitor, CompetitorModel>
) : CompetitorService {

    override fun getForLeague(competitorId: String, leagueId: String): Competitor {
        val competitor: Competitor = getById(competitorId)
        if (competitor.leagueId != leagueId)
            throw IllegalStateException("Competitor $competitorId is not assigned to league $leagueId")
        return competitor
    }

    override fun get(competitorId: String): CompetitorModel {
        val competitor: Competitor = getById(competitorId)
        return mapper.toModel(competitor)
    }

    private fun getById(competitorId: String): Competitor {
        return repository.findById(competitorId) ?: throw IllegalStateException("Competitor $competitorId is not found")
    }

    override fun getListForLeague(leagueId: String): List<CompetitorModel> {
        val competitors = repository.findByLeagueId(leagueId)
        return competitors.map { competitor -> mapper.toModel(competitor) }
    }

    override fun create(leagueId: String, username: String): CompetitorModel {
        val competitor = repository.save(Competitor(leagueId, username))
        return mapper.toModel(competitor)
    }

    override fun updateStatistic(firstCompetitor: Competitor, secondCompetitor: Competitor, game: Game) {
        firstCompetitor.updateStatistics(game.playerOne, game.playerTwo.score, game.dateTime)
        repository.save(firstCompetitor)
        secondCompetitor.updateStatistics(game.playerTwo, game.playerOne.score, game.dateTime)
        repository.save(secondCompetitor)
    }
}
