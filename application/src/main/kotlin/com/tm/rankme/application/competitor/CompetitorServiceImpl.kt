package com.tm.rankme.application.competitor

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.game.Game
import org.springframework.stereotype.Service

@Service
internal class CompetitorServiceImpl(private val repository: CompetitorRepository) : CompetitorService {
    override fun getCompetitor(id: String, leagueId: String): Competitor {
        val competitor = repository.findById(id) ?: throw IllegalStateException("Competitor $id is not found")
        if (competitor.leagueId != leagueId)
            throw IllegalStateException("Competitor $id is not assigned to league $leagueId")
        return competitor
    }

    override fun getByLeagueId(leagueId: String): List<Competitor> {
        return repository.findByLeagueId(leagueId)
    }

    override fun updateCompetitorsStatistic(firstCompetitor: Competitor, secondCompetitor: Competitor, game: Game) {
        firstCompetitor.updateStatistics(game.playerOne, game.playerTwo.score, game.dateTime)
        repository.save(firstCompetitor)
        secondCompetitor.updateStatistics(game.playerTwo, game.playerOne.score, game.dateTime)
        repository.save(secondCompetitor)
    }
}
