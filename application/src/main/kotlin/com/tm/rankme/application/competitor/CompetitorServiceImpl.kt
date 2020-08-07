package com.tm.rankme.application.competitor

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.competitor.CompetitorRepository
import com.tm.rankme.domain.game.Game
import org.springframework.stereotype.Service

@Service
internal class CompetitorServiceImpl(
    private val repository: CompetitorRepository
) : CompetitorService {

    override fun getForLeague(competitorId: String, leagueId: String): Competitor {
        val competitor = get(competitorId)
        if (competitor.leagueId != leagueId)
            throw IllegalStateException("Competitor $competitorId is not assigned to league $leagueId")
        return competitor
    }

    override fun get(competitorId: String): Competitor {
        val competitor = repository.findById(competitorId)
        return competitor ?: throw IllegalStateException("Competitor $competitorId is not found")
    }

    override fun getListForLeague(leagueId: String): List<Competitor> {
        return repository.findByLeagueId(leagueId)
    }

    override fun create(leagueId: String, username: String): Competitor {
        val competitor = Competitor(leagueId, username)
        return repository.save(competitor)
    }

    override fun updateStatistic(firstCompetitor: Competitor, secondCompetitor: Competitor, game: Game) {
        firstCompetitor.updateStatistics(game.playerOne, game.playerTwo.score, game.dateTime)
        repository.save(firstCompetitor)
        secondCompetitor.updateStatistics(game.playerTwo, game.playerOne.score, game.dateTime)
        repository.save(secondCompetitor)
    }
}
