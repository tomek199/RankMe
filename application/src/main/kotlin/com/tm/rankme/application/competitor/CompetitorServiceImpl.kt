package com.tm.rankme.application.competitor

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.game.Game
import org.springframework.stereotype.Service

@Service
internal class CompetitorServiceImpl(
    private val repository: CompetitorRepository
) : CompetitorService {

    override fun getCompetitorForLeague(competitorId: String, leagueId: String): Competitor {
        val competitor = getCompetitor(competitorId)
        if (competitor.leagueId != leagueId)
            throw IllegalStateException("Competitor $competitorId is not assigned to league $leagueId")
        return competitor
    }

    override fun getCompetitor(competitorId: String): Competitor {
        val competitor = repository.findById(competitorId)
        return competitor ?: throw IllegalStateException("Competitor $competitorId is not found")
    }

    override fun getCompetitors(leagueId: String): List<Competitor> {
        return repository.findByLeagueId(leagueId)
    }

    override fun saveCompetitor(competitor: Competitor): Competitor {
        return repository.save(competitor)
    }

    override fun updateCompetitorsStatistic(firstCompetitor: Competitor, secondCompetitor: Competitor, game: Game) {
        firstCompetitor.updateStatistics(game.playerOne, game.playerTwo.score, game.dateTime)
        repository.save(firstCompetitor)
        secondCompetitor.updateStatistics(game.playerTwo, game.playerOne.score, game.dateTime)
        repository.save(secondCompetitor)
    }
}
